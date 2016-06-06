package sotechat.service;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import sotechat.controller.SubscribeEventListener;
import sotechat.wrappers.ProStateResponse;
import sotechat.wrappers.UserStateResponse;
import sotechat.data.Mapper;
import sotechat.data.SessionRepo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static sotechat.util.Utils.get;

/**
 * Kayttajan tilan kasittelyyn liittyva logiikka.
 */
@Service
public class StateService {


    /** Mapperilta voi esim. kysya "mika username on ID:lla x?". */
    private final Mapper mapperService;

    /** QueueService. */
    private final QueueService queueService;

    /** SubScribeEventHandler. */
    private final ApplicationListener subscribeEventListener;

    /** Session Repository. */
    private final SessionRepo sessionRepo;

    /** Channel where queue status is broadcasted. */
    public static final String QUEUE_BROADCAST_CHANNEL = "QBCC";



    /** Spring taikoo tassa konstruktorissa Singleton-instanssit palveluista.
     *
     * @param pMapper Olio, johon talletetaan tiedot kayttajien id:ista
     * ja kayttajanimista, ja josta voidaan hakea esim. kayttajanimi
     * kayttaja-id:n perusteella.
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

        /** Varmistetaan, etta sessionissa on asianmukaiset attribuutit. */
        sessionRepo.updateSessionAttributes(session, professional);

        /** Kaivetaan sessionista tiedot muuttujiin. */
        String state = get(session, "state");
        String username = get(session, "username");
        String userId = get(session, "userId");
        String category = get(session, "category");
        String channel = get(session, "channelId");
        /** Note: state on muuttujista ainoa, joka on AINA relevantti.
         Esim. username voi aluksi olla "UNKNOWN" tms. */

        /** Paketoidaan muuttujat StateResponseen, joka kaannetaan JSONiksi. */
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

        /** Varmistetaan, etta sessionissa on asianmukaiset attribuutit. */
        sessionRepo.updateSessionAttributes(session, professional);

        /** Kaivetaan sessionista tiedot muuttujiin. */
        String state = get(session, "state");
        String username = get(session, "username");
        String userId = get(session, "userId");
        String qbcc = QUEUE_BROADCAST_CHANNEL;
        String online = "true"; // TODO
        String channelIds = get(session, "channelIds");
        /** Note: ammattilaisella kaikki attribuutit relevantteja aina. */

        System.out.println("Hoitajan kanavat: " + channelIds);

        /** Paketoidaan muuttujat StateResponseen, joka kaannetaan JSONiksi. */
        return new ProStateResponse(
                state, username, userId, qbcc, online, channelIds);
    }


    /** Logiikka mita tehdaan, kun tulee pyynto liittya jonoon.
     * @param request req
     * @return ret
     * @throws IOException sdfsdf
     */
    public final String respondToJoinPoolRequest(
            final HttpServletRequest request
            ) throws IOException {

        HttpSession session = request.getSession();
        /** Tehdaan JSON-objekti clientin lahettamasta JSONista. */
        String jsonString = request.getReader().readLine();
        JsonParser parser = new JsonParser();
        JsonObject payload = parser.parse(jsonString).getAsJsonObject();
        String username = payload.get("username").getAsString();
        String startMessage = payload.get("startMessage").getAsString();
        String channelId = get(session, "channelId");

        System.out.println("     id(joinPool) = " + session.getId() + " , username = " + username);

        /** Tarkistetaan etta aiempi tila on "start". */
        if (!get(session, "state").equals("start")) {
            /** String (ei JSON), jotta AngularJS osaa ohjata fail-metodille. */
            return "Denied join pool request due to bad state.";
        }

        /** Tarkistetaan, ettei nimimerkki ole rekisteroity ammattilaiselle. */
        String id = "" + mapperService.getIdFromRegisteredName(username);
        if (!id.isEmpty() && mapperService.isUserProfessional(id)) {
            /** String (ei JSON) (AngularJS varten) */
            return "Denied join pool request due to reserved username.";
        }

        /** Tarkistetaan, ettei kanavalla ole toista kayttajaa samalla
         * nimimerkilla (olennainen vasta vertaistukichatissa). */
        SubscribeEventListener customClass =
                (SubscribeEventListener) subscribeEventListener;
        String channelIdWithPath = "/toClient/chat/" + channelId;
        List<HttpSession> list = customClass.getSubscribers(channelIdWithPath);
        for (HttpSession other : list) {
            if (get(other, "username").equals(username)) {
                /** String (ei JSON) (AngularJS varten) */
                return "Denied join pool request. Username already on channel.";
            }
        }

        /** Hyvaksytaan kayttajan valitsema nimimerkki. */
        session.setAttribute("username", username);

        /** Mapataan nimimerkki ja kayttajatunnus. */
        String userId = get(session, "userId");
        mapperService.mapUsernameToId(userId, username);

        /** Asetetaan kayttaja jonoon odottamaan palvelua. */
        String category = get(session, "category");
        queueService.addToQueue(channelId, category, username);
        session.setAttribute("state", "queue");

        /** JSON-muodossa, jotta AngularJS osaa ohjata success-metodille. */
        return "{\"content\":\"OK, please request new state now.\"}";
    }

    public String popQueue(String channelId) {
        System.out.println("Opening channel Id: " + channelId);
        queueService.removeFromQueue(channelId);

        /** Set state of members in channel to "chat". */
        SubscribeEventListener customClass =
                (SubscribeEventListener) subscribeEventListener;
        String channelIdWithPath = "/toClient/chat/" + channelId;
        List<HttpSession> list = customClass.getSubscribers(channelIdWithPath);
        for (HttpSession member : list) {
            member.setAttribute("state", "chat");
        }

        return "{\"content\":\"channel activated. request new state now.\"}";
    }

}
