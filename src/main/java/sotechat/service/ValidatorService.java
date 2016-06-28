package sotechat.service;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import sotechat.data.Channel;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;
import sotechat.wrappers.MsgToServer;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import static sotechat.config.StaticVariables.QUEUE_BROADCAST_CHANNEL;

/** Luokan tehtava on validoida netista tuleva data.
 *  (hyvaksya/siivota/kieltaytya vastaanottamasta). */
@Service
public class ValidatorService {

    /** Mapper. */
    private Mapper mapper;

    /** Session Repo. */
    private SessionRepo sessionRepo;

    /**
     * WebSocket-osoitteessa olevien kauttaviivalla eroteltujen osien lukumaara.
     */
    private static final int CHANNEL_PATH_LENGTH = 4;

    /**
     * Kuinka mones alkio channelId on taulukossa, joka on muodostettu
     * WebSocket-osoitteesta kauttaviivan perusteella erottelemalla.
     */
    private static final int POSITION_OF_CHANNEL_ID_IN_CHANNEL_PATH = 3;
    /**
     * Konstruktori.
     * @param pMapper mapper
     * @param pSessionRepo ses
     */
    @Autowired
    public ValidatorService(
            final Mapper pMapper,
            final SessionRepo pSessionRepo
    ) {
        this.mapper = pMapper;
        this.sessionRepo = pSessionRepo;
    }

    /** Onko chattiin tuleva viesti vaarennetty?
     * @param msgToServer msgToServer
     * @param accessor accessor
     * @return tyhja String jos viesti vaikuttaa aidolta,
     *         muussa tapauksessa virheilmoitus-String.
     */
    public final String isMessageFraudulent(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor accessor
    ) {
        String sessionId = getSessionIdFrom(accessor);
        Principal principal = accessor.getUser();

        /* Overloadattu metodi testauksen helpottamiseksi. */
        return isMessageFraudulent(msgToServer, sessionId, principal);
    }

    /**
     * Onko chattiin tuleva viesti vaarennetty?
     * @param msgToServer p
     * @param sessionId p
     * @param principal p
     * @return tyhja String jos viesti vaikuttaa aidolta,
     *         muussa tapauksessa virheilmoitus-String.
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

        /** Puuttuuko viestin lahettajalta kuunteluoikeus kanavalle? */
        String channelId = msgToServer.getChannelId();
        Channel channel = mapper.getChannel(channelId);
        if (!channel.hasActiveUser(userId)) { //TODO NULL.
            return "Lahettajalta puuttuu kuunteluoikeus kanavalle";
        }

        /** Viesti vaikuttaa aidolta. */
        return "";
    }

    /** Validoi pyynnon hakea lokeja.
     * @param principal autentikaatiotiedot
     * @param req pyynto
     * @param channelId channelId
     * @return tyhja String, jos sallitaan pyynto. Muuten virheilmoitus.
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

        /** Sallitaan pyynto. */
        return "";
    }


    /** Sallitaanko subscription eli kanavan kuuntelu?.
     * Jos sallitaan, palauttaa tyhjan Stringin.
     * Jos ei sallita, palauttaa virheilmoituksen.
     * HUOM: Mockito vaatii, ettei metodi ole final!
     * @param acc pyynnon tiedot
     * @return virheilmoitus Stringina jos ei sallita pyyntoa.
     */
    public String validateSubscription(
            final StompHeaderAccessor acc
    ) {
        Principal principal = acc.getUser();
        String sessionId = getSessionIdFrom(acc);
        String channelIdWithPath = acc.getDestination();
        String prefix = "Validate subscription for channel " + channelIdWithPath
                + " by session id " + sessionId + " ### ";

        /** Kelvollinen sessio? */
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        if (session == null) {
            return prefix + "Session is null";
        }

        /** Ammattilaiskayttaja? */
        if (session.isPro()) {
            /** Loytyyko autentikaatio myos principal-oliosta? */
            if (principal == null) {
                return prefix + "Session belongs to pro but user not auth'd";
            }
            String qbcc = "/toClient/" + QUEUE_BROADCAST_CHANNEL;
            if (channelIdWithPath.equals(qbcc)) {
                /** Sallitaan - ammattilaiskayttaja saa kuunnella QBCC. */
                return "";
            }
            if (channelIdWithPath.startsWith("/toClient/queue/")) {
                /** Sallitaan hoitajien kuunnella kaikkia jonokanavia. */
                return "";
            }
        }

        /** Kielletaan subscribaus kaikkialle poislukien:
         * /toClient/queue/channelId
         * /toClient/chat/channelId
         * Tarkistetaan, onko kayttajalla/pro:lla oikeutta kanavalle.
         * TODO Refaktoroi regexilla.
         * */
        String[] splitted = channelIdWithPath.split("/");
        if (splitted.length != CHANNEL_PATH_LENGTH) {
            return prefix + "Invalid channel path (1): " + channelIdWithPath;
        }
        if (!"toClient".equals(splitted[1])) {
            return prefix + "Invalid channel path (2): " + channelIdWithPath;
        }
        if (!"queue".equals(splitted[2])
                && !"chat".equals(splitted[2])) {
            return prefix + "Invalid channel path (3): " + channelIdWithPath;
        }
        String channelId = splitted[POSITION_OF_CHANNEL_ID_IN_CHANNEL_PATH];

        if (!session.hasAccessToChannel(channelId)) {
            return prefix
                    + "Ei oikeutta kuunnella kanavaa! userId "
                    + session.get("userId");
        }

        /** Sessiolla on oikeus kuunnella kanavaa. */
        return "";
    }

    /** Validointi pyynnolle liittya jonoon.
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
            /** Hoitaja yrittaa liittya pooliin asiakkaana. */
            return "Denied join pool request for professional";
        }

        /** Clientin session tarkistus. */
        String sessionId = request.getSession().getId();
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        if (session == null) {
            return "Denied due to missing or invalid session ID.";
        }

        /** Kaivetaan JSON-objektista attribuutteja muuttujiin. */
        String username = payload.get("username").getAsString();
        String channelId = session.get("channelId");

        /** Tarkistetaan etta aiempi tila on "start". */
        if (!session.get("state").equals("start")) {
            return "Denied join pool request due to bad state.";
        }

        /** Tarkistetaan, ettei nimimerkki ole rekisteroity ammattilaiselle. */
        if (mapper.isUsernameReserved(username)) {
            return "Denied join pool request due to reserved username.";
        }

        /** Tarkistetaan, ettei kanavalla ole toista kayttajaa samalla
         * nimimerkilla (olennainen vasta 3+ henkilon chatissa). */
        Channel channel = mapper.getChannel(channelId);
        for (Session other : channel.getCurrentSubscribers()) {
            if (other.get("username").equals(username)) {
                return "Denied join pool request. Username already on channel.";
            }
        }

        /** Sallitaan pyynto. */
        return "";
    }

    /** Validoi pyynnon poistua chat-kanavalta.
     * @param sessionId sessionId
     * @param professional pro
     * @param channelId channelId
     * @return true jos salltiaan pyynto
     */
    public final boolean validateLeave(
            final String sessionId,
            final Principal professional,
            final String channelId
    ) {
        /** Clientin session tarkistus. */
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        if (session == null) {
            return false;
        }

        /** Jos sessioId kuuluu kirjautuneelle kayttajalle,
         * varmistetaan viela autentikointi. */
        if (session.isPro()) {
            if (professional == null) {
                /** Joku esittaa hoitajaa varastetulla sessio-cookiella. */
                return false;
            }
            if (!professional.getName().equals(session.get("username"))) {
                /** Yksi hoitaja esittaa toista hoitajaa. */
                return false;
            }
        }

        if (!session.hasAccessToChannel(channelId)) {
            /** Ei voi poistua kanavalta, jolla ei ole. */
            return false;
        }

        return true;
    }

    /**
     * Validoi pyynnon muuttaa hoitajan online-tilaa.
     * @param professional autentikaatiotiedot
     * @param req pyynnon tiedot
     * @param onlineStatus asetettava status "true" tai "false" Stringina
     * @return virheilmoitus Stringina tai tyhja String jos pyynto ok.
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

        /** Hyvaksytaan pyynto. */
        return "";
    }

    /** Validoi admininin pyynnon lisata uusi ammattilaiskayttaja.
     * @param encodedPersonJson lisattavan tiedot encoodattuna jsonina.
     * @param personRepo spring hajoaa, jos tama on luokkainstanssin
     *                   dependency eika parametri
     * @return virheilmoitus String tai tyhja String jos pyynto hyvaksytaan.
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

    /** Palauttaa sessionId:n Stringina tai tyhjan Stringin.
     * @param headerAccessor mista id kaivetaan
     * @return sessionId String
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
