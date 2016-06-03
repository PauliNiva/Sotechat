package sotechat.service;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import sotechat.ProStateResponse;
import sotechat.UserStateResponse;
import sotechat.data.Mapper;
import sotechat.data.SessionRepo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import static sotechat.util.Utils.get;

/**
 * Käyttäjän tilan käsittelyyn liittyvä logiikka.
 */
@Service
public class StateService {


    /** Mapperilta voi esim. kysyä "mikä username on ID:llä x?". */
    private final Mapper mapperService;

    /** QueueService. */
    private final QueueService queueService;

    /** SubScribeEventHandler. */
    private final ApplicationListener subscribeEventListener;

    /** Session Repository. */
    private final SessionRepo sessionRepo;

    /** Channel where queue status is broadcasted. */
    private static final String QUEUE_BROADCAST_CHANNEL = "QBCC";



    /** Spring taikoo tässä konstruktorissa Singleton-instanssit palveluista.
     *
     * @param pMapper Olio, johon talletetaan tiedot käyttäjien id:istä
     * ja käyttäjänimistä, ja josta voidaan hakea esim. käyttäjänimi
     * käyttäjä-id:n perusteella.
     * @param pQueueService queueService
     * @param subscribeEventListener dfojfdoidfjo
     */
    @Autowired
    public StateService(
            final Mapper pMapper,
            /* HUOM: Spring ei salli "pSubsc..." tyyppista nimentaa tuolle. */
            final ApplicationListener subscribeEventListener,
            final QueueService pQueueService,
            final SessionRepo pSessionRepo
    ) {
        this.mapperService = pMapper;
        this.subscribeEventListener = subscribeEventListener;
        this.queueService = pQueueService;
        this.sessionRepo = pSessionRepo;
    }

    /** Logiikka miten vastataan customerClientin state requestiin.
     * @param req request
     * @param professional kirjautumistiedot - kirjautumaton on null
     * @return UserStateResponse
     */
    public final UserStateResponse respondToUserStateRequest(
            final HttpServletRequest req,
            final Principal professional
    ) {
        HttpSession session = req.getSession();
        System.out.println("     State request from customerClient " + session.getId());
        sessionRepo.mapHttpSessionToSessionId(session.getId(), session);

        /** Varmistetaan, että sessionissa on asianmukaiset attribuutit. */
        sessionRepo.updateSessionAttributes(session, professional);

        /** Kaivetaan sessionista tiedot muuttujiin. */
        String state = get(session, "state");
        String username = get(session, "username");
        String userId = get(session, "userId");
        String category = get(session, "category");
        String channel = get(session, "channelId");
        /** Note: state on muuttujista ainoa, joka on AINA relevantti.
         Esim. username voi aluksi olla "UNKNOWN" tms. */

        /** Paketoidaan muuttujat StateResponseen, joka käännetään JSONiksi. */
        return new UserStateResponse(state, username, userId, category, channel);
    }


    /** Logiikka miten vastataan proClientin state requestiin.
     * @return UserStateResponse
     */
    public final ProStateResponse respondToProStateRequest(
            final HttpServletRequest req,
            final Principal professional
    ) {

        HttpSession session = req.getSession();
        System.out.println("     State request from proClient " + session.getId());

        /** Varmistetaan, että sessionissa on asianmukaiset attribuutit. */
        sessionRepo.updateSessionAttributes(session, professional);

        /** Kaivetaan sessionista tiedot muuttujiin. */
        String state = get(session, "state");
        String username = get(session, "username");
        String userId = get(session, "userId");
        String qbcc = QUEUE_BROADCAST_CHANNEL;
        String online = "true"; // TODO
        String channelIds = get(session, "channelIds");
        /** Note: ammattilaisella kaikki attribuutit relevantteja aina. */

        /** Paketoidaan muuttujat StateResponseen, joka käännetään JSONiksi. */
        return new ProStateResponse(
                state, username, userId, qbcc, online, channelIds);
    }


    /** Logiikka mitä tehdään, kun tulee pyyntö liittyä pooliin (jonoon).
     * @param request req
     * @return ret
     * @throws IOException sdfsdf
     */
    public final String respondToJoinPoolRequest(
            final HttpServletRequest request
            ) throws IOException {

        HttpSession session = request.getSession();
        /** Tehdään JSON-objekti clientin lähettämästä JSONista. */
        String jsonString = request.getReader().readLine();
        JsonParser parser = new JsonParser();
        JsonObject payload = parser.parse(jsonString).getAsJsonObject();
        String username = payload.get("username").getAsString();

        System.out.println("     id(joinPool) = " + session.getId() + " , username = " + username);

        if (!get(session, "state").equals("start")) {
            /** Ei JSON-muodossa, jotta AngularJS osaa ohjata fail-metodille. */
            return "Denied join pool request due to bad state.";
        }
        if (false) {
            // TODO: validoi username.
            /** Ei JSON-muodossa, jotta AngularJS osaa ohjata fail-metodille. */
            return "Denied join pool request due to reserved username.";
        }

        String userId = get(session, "userId");
        /** Käyttäjä valitsi (uuden) nimen. */
        session.setAttribute("username", username);
        mapperService.mapUsernameToId(userId, username);
        /** Asetetaan käyttäjä pooliin. */
        String channelId = get(session, "channelId");
        String category = get(session, "category");
        queueService.addToQueue(channelId, category, username);
        session.setAttribute("state", "queue");
        /** JSON-muodossa, jotta AngularJS osaa ohjata success-metodille. */
        return "{\"content\":\"OK, please request new state now.\"}";
    }

    public String popQueue(String channelId) {
        System.out.println("Opening channel Id: " + channelId);
        queueService.removeFromQueue(channelId);

        return "{\"content\":\"channel activated. request new state now.\"}";
    }

    /** Getter.
     * @return QBCC.
     */
    public String getQueueBroadcastChannel() {
        return QUEUE_BROADCAST_CHANNEL;
    }

    public String getQueueAsJson() {
        return queueService.toString();
    }
}
