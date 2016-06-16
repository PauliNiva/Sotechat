package sotechat.service;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import sotechat.controller.SubscribeEventListener;
import sotechat.data.ChatLogger;
import sotechat.data.Session;
import sotechat.wrappers.MsgToClient;
import sotechat.data.Mapper;
import sotechat.data.SessionRepo;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Kayttajan tilan kasittelyyn liittyva logiikka.
 */
@Service
public class StateService {


    /** Mapperilta voi esim. kysya "mika username on ID:lla x?". */
    private final Mapper mapperService;

    /** QueueService. */
    private final QueueService queueService;

    /** Chat Log Service. */
    private final ChatLogger chatLogger;

    /** SubScribeEventHandler. */
    private final SubscribeEventListener subscribeEventListener;

    /** Session Repository. */
    private final SessionRepo sessionRepo;

    /** Database service */
    private final DatabaseService databaseService;

    /** Channel where queue status is broadcasted. */
    public static final String QUEUE_BROADCAST_CHANNEL = "QBCC";

    /** Konstruktori.
     * @param pMapper mapper
     * @param subscribeEventListener subscribeEventListener
     * @param pQueueService queueService
     * @param pChatLogger chatLogger
     * @param pSessionRepo sessionRepo
     */
    @Autowired
    public StateService(
            final Mapper pMapper,
            /* HUOM: Spring ei salli "pSubsc..." tyyppista nimentaa tuolle. */
            final SubscribeEventListener subscribeEventListener,
            final QueueService pQueueService,
            final ChatLogger pChatLogger,
            final SessionRepo pSessionRepo,
            final DatabaseService pDatabaseService
    ) {
        this.mapperService = pMapper;
        this.subscribeEventListener = subscribeEventListener;
        this.queueService = pQueueService;
        this.chatLogger = pChatLogger;
        this.sessionRepo = pSessionRepo;
        this.databaseService = pDatabaseService;
    }

    /** Logiikka mita tehdaan, kun tulee pyynto liittya jonoon.
     * @param request taalta saadaan session tiedot
     * @return String "Denied..." tai "OK..."
     * @throws IOException mika poikkeus
     */
    public final synchronized String respondToJoinPoolRequest(
            final HttpServletRequest request
            ) throws Exception {

        String sessionId = request.getSession().getId();
        System.out.println("Session id = " + sessionId);
        Session session = sessionRepo.getSessionObj(sessionId);
        if (session == null) {
            return "Denied due to missing or invalid session ID.";
        }
        /** Tehdaan JSON-objekti clientin lahettamasta JSONista. */
        String jsonString = request.getReader().readLine();
        JsonParser parser = new JsonParser();
        JsonObject payload = parser.parse(jsonString).getAsJsonObject();
        String username = payload.get("username").getAsString();
        String startMessage = payload.get("startMessage").getAsString();
        String channelId = session.get("channelId");

        System.out.println("REQUESTED USERNAME " + username + " with start message " + startMessage);

        /** Tarkistetaan etta aiempi tila on "start". */
        if (!session.get("state").equals("start")) {
            return "Denied join pool request due to bad state.";
        }

        /** Tarkistetaan, ettei nimimerkki ole rekisteroity ammattilaiselle. */
        String id = "" + mapperService.getIdFromRegisteredName(username);
        if (!id.isEmpty() && mapperService.isUserProfessional(id)) {
            return "Denied join pool request due to reserved username.";
        }
        /** Tarkistetaan, ettei kanavalla ole toista kayttajaa samalla
         * nimimerkilla (olennainen vasta vertaistukichatissa). */
        String channelIdWithPath = "/toClient/chat/" + channelId;
        List<Session> list = subscribeEventListener
                .getSubscribers(channelIdWithPath);
        for (Session other : list) {
            if (other.get("username").equals(username)) {
                return "Denied join pool request. Username already on channel.";
            }
        }

        /** Hyvaksytaan kayttajan valitsema nimimerkki. */
        session.set("username", username);

        /** Mapataan nimimerkki ja kayttajatunnus. */
        String userId = session.get("userId");
        mapperService.mapUsernameToId(userId, username);

        /** Asetetaan kayttaja jonoon odottamaan palvelua. */
        String category = session.get("category");
        queueService.addToQueue(channelId, category, username);
        session.set("state", "queue");

        /** Luodaan tietokantaan uusi keskustelu */
        databaseService.createConversation(startMessage, username, channelId,
                category);

        /** Kirjatataan aloitusviesti kanavan lokeihin. Viestia
         * ei tarvitse viela lahettaa, koska kanavalla ei ole ketaan.
         * Kun kanavalle liittyy joku, lokit lahetetaan sille. */
        String timeStamp = new DateTime().toString();
        MsgToClient msg = new MsgToClient(
                username, channelId, timeStamp, startMessage);
        chatLogger.log(msg);
        return "OK, please request new state now.";
    }

    /** Kun meille saapuu pyynto nostaa jonosta chatti.
     * @param channelId kanavaId
     * @param accessor taalta autentikaatiotiedot
     * @return null jos poppaus epaonnistuu,
     *          JSON {"content":"channel activated."} jos poppaus onnistuu.
     */
    public final synchronized String popQueue(
            final String channelId,
            final SimpMessageHeaderAccessor accessor
            ) throws Exception {
        if (queueService.removeFromQueue(channelId) == null) {
            /** Poppaus epaonnistui. Ehtiko joku muu popata samaan aikaan? */
            return "";
        }
        String sessionId =  accessor.getSessionAttributes()
                .get("SPRING.SESSION.ID").toString();
        Session session = sessionRepo.getSessionObj(sessionId);

        /** Lisataan popattu kanava poppaajan kanaviin. */
        session.addChannel(channelId);

        /** Muutetaan popattavan kanavan henkiloiden tilaa. */
        changeParticipantsState(channelId);

        /** Lisätään poppaaja tietokannassa olevaan keskusteluun */
        databaseService.addPersonToConversation(
                session.get("userId"), channelId);

        /** Onnistui, palautetaan JSONi. */
        return "{\"content\":\"channel activated.\"}";
    }

    /** Muokataan popattavan kanavan sessioiden tilaksi "chat".
     * @param channelId kanavan id
     */
    private final void changeParticipantsState(
            String channelId
            ) throws Exception {
        String channelIdWithPath = "/toClient/queue/" + channelId;

        List<Session> list = subscribeEventListener.
                getSubscribers(channelIdWithPath);

        for (Session member : list) {
            member.set("state", "chat");
        }
    }

}
