package sotechat.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import sotechat.controller.SubscribeEventListener;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.wrappers.MsgToServer;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Set;

import static sotechat.config.StaticVariables.QUEUE_BROADCAST_CHANNEL;

/** Luokan tehtava on validoida netista tuleva data
 *  (hyvaksya/siivota/kieltaytya vastaanottamasta). */
@Service
public class ValidatorService {

    /** Mapper. */
    private Mapper mapper;

    /** Session Repo. */
    private SessionRepo sessionRepo;

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
     * @return true jos sallitaan
     */
    public final boolean isMessageFraudulent(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor accessor
    ) {
        String sessionId = getSessionIdFrom(accessor);
        Session session = sessionRepo.getSessionObj(sessionId);
        if (session == null) {
            /** Kelvoton sessio, hylataan viesti. */
            return true;
        }
        String userId = msgToServer.getUserId();
        if (!mapper.isUserIdMapped(userId)) {
            /** Kelvoton ID, hylataan viesti. */
            return true;
        }
        if (mapper.isUserProfessional(userId)) {
            /** ID kuuluu ammattilaiselle, varmistetaan etta on kirjautunut. */

            if (accessor.getUser() == null) {
                /** Ei kirjautunut, hylataan viesti. */
                return true;
            }
            String username = accessor.getUser().getName();
            String authId = mapper.getIdFromRegisteredName(username);
            if (!userId.equals(authId)) {
                /** Kirjautunut ID eri kuin viestiin merkitty lahettajan ID. */
                return true;
            }
        }

        /** Puuttuuko viestin lahettajalta kuunteluoikeus kanavalle? */
        String channelId = msgToServer.getChannelId();
        String chatPrefix = "/toClient/chat/";
        String channelIdWithPath = chatPrefix + channelId;
        if (!mapper.getSubscribers(channelIdWithPath).contains(session)) {
            return true;
        }

        return false;
    }


    /** Sallitaanko subscription eli kanavan kuuntelu?.
     * Jos sallitaan, palauttaa tyhjan Stringin.
     * Jos ei sallita, palauttaa virheilmoituksen.
     * @param principal autentikaatiotiedot
     * @param sessionId sessioId
     * @param channelIdWithPath channelIdWithPath
     * @return virheilmoitus Stringina jos ei sallita pyyntoa.
     */
    public final String validateSubscription(
            final Principal principal,
            final String sessionId,
            final String channelIdWithPath
    ) {
        String prefix = "Validate subscription for channel " + channelIdWithPath
                + " by session id " + sessionId + " ### ";

        /** Kelvollinen sessio? */
        Session session = sessionRepo.getSessionObj(sessionId);
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
         * TODO: Refaktoroi regexilla. */
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

        if (!session.isOnChannel(channelId)) {
            return prefix + "Ei oikeutta kuunnella kanavaa!";
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
        if (professional != null) {
            /** Hoitaja yrittaa liittya pooliin asiakkaana. */
            return "{\"content\":\"Denied join "
                    + "pool request for professional.\"}";
        }

        /** Clientin session tarkistus. */
        String sessionId = request.getSession().getId();
        Session session = sessionRepo.getSessionObj(sessionId);
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
        String id = "" + mapper.getIdFromRegisteredName(username);
        if (!id.isEmpty() && mapper.isUserProfessional(id)) {
            return "Denied join pool request due to reserved username.";
        }

        /** Tarkistetaan, ettei kanavalla ole toista kayttajaa samalla
         * nimimerkilla (olennainen vasta 3+ henkilon chatissa). */
        String channelIdWithPath = "/toClient/chat/" + channelId;
        Set<Session> list = mapper.getSubscribers(channelIdWithPath);
        for (Session other : list) {
            if (other.get("username").equals(username)) {
                return "Denied join pool request. Username already on channel.";
            }
        }

        /** Sallitaan pyynto. */
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
