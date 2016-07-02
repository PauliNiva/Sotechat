package sotechat.service;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import sotechat.data.Channel;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;
import sotechat.wrappers.MsgToServer;

/**
 * Validoi palvelimelle tulevan datan
 * (hyvaksya/siivota/kieltaytya vastaanottamasta).
 */
@Service
public class ValidatorService {

    /**
     * Mapper.
     */
    private Mapper mapper;

    /**
     * Session Repo.
     */
    private SessionRepo sessionRepo;

    /**
     * Konstruktori.
     *
     * @param pMapper p
     * @param pSessionRepo p
     */
    @Autowired
    public ValidatorService(
            final Mapper pMapper,
            final SessionRepo pSessionRepo
    ) {
        this.mapper = pMapper;
        this.sessionRepo = pSessionRepo;
    }

    /**
     * Validoi keskusteluun tulevan viestin.
     *
     * @param msgToServer msgToServer
     * @param accessor accessor
     * @return Tyhja <code>String</code> jos viesti vaikuttaa aidolta,
     *         muussa tapauksessa virheilmoitus.
     */
    public final String isMessageFraudulent(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor accessor
    ) {
        String sessionId = getSessionIdFrom(accessor);
        Principal principal = accessor.getUser();

        return isMessageFraudulent(msgToServer, sessionId, principal);
    }

    /**
     * Validoi keskusteluun tulevan viestin.
     *
     * @param msgToServer p
     * @param sessionId p
     * @param principal p
     * @return Tyhja <code>String</code>  jos viesti vaikuttaa aidolta,
     *         muussa tapauksessa virheilmoitus.
     */
    public final String isMessageFraudulent(
            final MsgToServer msgToServer,
            final String sessionId,
            final Principal principal
    ) {
        Session session = sessionRepo.getSessionFromSessionId(sessionId);

        if (session == null) {
            return "Kelvoton sessioId, hylataan viesti";
        }
        String userId = msgToServer.getUserId();
        if (sessionRepo.getSessionFromUserId(userId) != session) {
            return "Kelvoton userId, hylataan viesti";
        }
        if (session.isPro()) {
            /** ID kuuluu ammattilaiselle, varmistetaan etta on kirjautunut. */

            if (principal == null) {
                return "ID kuuluu pro:lle, lahettaja ei kirjautunut, hylataan";
            }
            String username = principal.getName();
            String authId = mapper.getIdFromRegisteredName(username);
            if (!userId.equals(authId)) {
                return "Lahettajaksi merkitty ID eri kuin autentikaation ID";
            }
        }

        /* Puuttuuko viestin lahettajalta kuunteluoikeus kanavalle? */
        String channelId = msgToServer.getChannelId();
        Channel channel = mapper.getChannel(channelId);
        if (!channel.hasActiveUser(userId)) {
            return "Lahettajalta puuttuu kuunteluoikeus kanavalle";
        }

        /* Viesti vaikuttaa aidolta. */
        return "";
    }

    /**
     * Validoi pyynnon hakea lokeja.
     *
     * @param principal autentikaatiotiedot
     * @param req pyynto
     * @param channelId channelId
     * @return Tyhja String, jos sallitaan pyynto. Muuten virheilmoitus.
     */
    public final String validateLogRequest(
            final Principal principal,
            final HttpServletRequest req,
            final String channelId
    ) {
        if (principal == null) {
            return "Unauthenticated user can't request logs!";
        }
        String sessionId = req.getSession().getId();
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        if (session == null) {
            return "Invalid session!";
        }
        Channel channel = mapper.getChannel(channelId);
        String userId = session.get("userId");
        if (!channel.hasHistoricUser(userId)) {
            return "Can't access other peoples' logs!";
        }

        /* Sallitaan pyynto. */
        return "";
    }


    /**
     * Sallitaanko polun kuuntelu?
     * Jos sallitaan, palauttaa tyhjan Stringin.
     * Jos ei sallita, palauttaa virheilmoituksen.
     *
     * @param acc Pyynnon tiedot.
     * @return Virheilmoitus Stringina jos ei sallita pyyntoa.
     */
    public String validateSubscription(
            final StompHeaderAccessor acc
    ) {
        Principal principal = acc.getUser();
        String sessionId = getSessionIdFrom(acc);
        String channelIdWithPath = acc.getDestination();
        String prefix = "Validate subscription for " + channelIdWithPath
                + " by session id " + sessionId + " ### ";

        /* Kelvollinen sessio? */
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        if (session == null) {
            return prefix + "Session is null";
        }

        /* Ammattilaiskayttaja? */
        if (session.isPro()) {
            /* Loytyyko autentikaatio myos principal-oliosta? */
            if (principal == null) {
                return prefix + "Session belongs to pro but user not auth'd";
            }
            if (channelIdWithPath.equals("/toClient/QBCC")) {
                /* Sallitaan ammattilaiskayttajalle
                 * jonon tilannepaivityksien kuuntelu. */
                return "";
            }
            if (channelIdWithPath.startsWith("/toClient/queue/")) {
                /* Sallitaan hoitajien kuunnella kaikkia jonokanavia. */
                return "";
            }
        }

        /**
         * Kielletaan kaikkien muiden polkujen tilaus, paitsi:
         * /toClient/queue/channelId
         * /toClient/chat/channelId
         */
        String[] splitted = channelIdWithPath.split("/");
        if (splitted.length != 4) {
            return prefix + "Invalid channel path (1): " + channelIdWithPath;
        }
        if (!"toClient".equals(splitted[1])) {
            return prefix + "Invalid channel path (2): " + channelIdWithPath;
        }
        if (!"queue".equals(splitted[2])
                && !"chat".equals(splitted[2])) {
            return prefix + "Invalid channel path (3): " + channelIdWithPath;
        }
        String channelId = splitted[3];

        if (!session.hasAccessToChannel(channelId)) {
            return prefix
                    + "Ei oikeutta kuunnella kanavaa! userId "
                    + session.get("userId");
        }

        /* Sessiolla on oikeus kuunnella kanavaa. */
        return "";
    }

    /**
     * Validointi pyynnolle liittya jonoon.
     *
     * @param request req
     * @param payload payload
     * @param professional pro
     * @return Jos pyynto hylataan, palautetaan virheilmoitus Stringina.
     *         Jos pyynto hyvaksytaan, palautetaan payload JSON-objektina.
     */
    public final String validateJoin(
            final HttpServletRequest request,
            final JsonObject payload,
            final Principal professional
    ) {
        if (sessionRepo.chatClosed()) {
            return "Denied join, no professionals available.";
        }
        if (professional != null) {
            /* Hoitaja yrittaa liittya queueen asiakkaana. */
            return "Denied join queue request for professional";
        }

        /* Clientin session tarkistus. */
        String sessionId = request.getSession().getId();
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        if (session == null) {
            return "Denied due to missing or invalid session ID.";
        }

        /* Kaivetaan JSON-objektista attribuutteja muuttujiin. */
        String username = payload.get("username").getAsString();
        String channelId = session.get("channelId");

        /* Tarkistetaan etta aiempi tila on "start". */
        if (!session.get("state").equals("start")) {
            return "Denied join queue request due to bad state.";
        }

        /* Tarkistetaan, ettei nimimerkki ole rekisteroity ammattilaiselle. */
        if (mapper.isUsernameReserved(username)) {
            return "Denied join queue request due to reserved username.";
        }

        /* Tarkistetaan, ettei kanavalla ole toista kayttajaa samalla
         * nimimerkilla (olennainen vasta 3+ henkilon chatissa). */
        Channel channel = mapper.getChannel(channelId);
        for (Session other : channel.getCurrentSubscribers()) {
            if (other.get("username").equals(username)) {
                return "Denied join queue request. " +
                        "Username already on channel.";
            }
        }

        /* Sallitaan pyynto. */
        return "";
    }

    /**
     * Validoi pyynnon poistua chat-kanavalta.
     *
     * @param sessionId sessionId
     * @param professional pro
     * @param channelId channelId
     * @return <code>true</code> jos salltiaan pyynto.
     */
    public final boolean validateLeave(
            final String sessionId,
            final Principal professional,
            final String channelId
    ) {
        /* Clientin session tarkistus. */
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        if (session == null) {
            return false;
        }

        /* Jos sessioId kuuluu kirjautuneelle kayttajalle,
         * varmistetaan viela autentikointi. */
        if (session.isPro()) {
            if (professional == null) {
                /* Joku esittaa hoitajaa varastetulla sessio-cookiella. */
                return false;
            }
            if (!professional.getName().equals(session.get("username"))) {
                /* Yksi hoitaja esittaa toista hoitajaa. */
                return false;
            }
        }

        if (!session.hasAccessToChannel(channelId)) {
            /* Ei voi poistua kanavalta, jolla ei ole. */
            return false;
        }

        return true;
    }

    /**
     * Validoi pyynnon muuttaa hoitajan online-tilaa.
     *
     * @param professional Autentikaatiotiedot.
     * @param req Pyynnon tiedot.
     * @param onlineStatus Asetettava status "true" tai "false" Stringina.
     * @return Virheilmoitus Stringina tai tyhja String jos pyynto ok.
     */
    public String validateOnlineStatusChangeRequest(
            final Principal professional,
            final HttpServletRequest req,
            final String onlineStatus
    ) {
        if (professional == null) {
            return "Unauthenticated user.";
        }
        String sessionId = req.getSession().getId();
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        if (session == null) {
            return "No session associated.";
        }
        if (!onlineStatus.equals("true") && !onlineStatus.equals("false")) {
            return "You can only set online status to true or false!";
        }

        /* Hyvaksytaan pyynto. */
        return "";
    }

    /**
     * Validoi yllapitajan pyynnon lisata uusi ammattilaiskayttaja.
     *
     * @param encodedPersonJson Lisattavan tiedot encoodattuna jsonina.
     * @param personRepo PersonRepo.
     * @return Virheilmoitus String tai tyhja String jos pyynto hyvaksytaan.
     */
    public String validateAddUserReq(
            final String encodedPersonJson,
            final PersonRepo personRepo
    ) {
        Person person = AdminService.makePersonFrom(encodedPersonJson);
        if (person == null) {
            return "Virheellinen muotoilu (joko encoodaus tai itse JSON)";
        }
        String loginName = person.getLoginName();
        String userName = person.getUserName();
        if (loginName == null
                || loginName.isEmpty()
                || person.getHashOfPasswordAndSalt() == null
                || person.getHashOfPasswordAndSalt().isEmpty()
                || userName == null
                || userName.isEmpty()
                || mapper.isUsernameReserved(userName)
                || personRepo.findByLoginName(loginName) != null) {
            return "Käyttäjää ei voitu lisätä. "
                    + "Tarkista, että kirjautumisnimi tai"
                    + "palvelunimi eivät ole jo varattuja.";
        }

        /* Hyvaksytaan pyynto. */
        return "";
    }

    /**
     * Palauttaa sessionId:n Stringina tai tyhjan Stringin.
     *
     * @param headerAccessor Id:n lahde.
     * @return sessionId String.
     */
    private String getSessionIdFrom(
            final SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            return headerAccessor
                    .getSessionAttributes()
                    .get("SPRING.SESSION.ID")
                    .toString();
        } catch (Exception e) {
            return "";
        }
    }

}
