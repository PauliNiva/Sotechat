package sotechat.service;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import sotechat.ProStateResponse;
import sotechat.UserStateResponse;
import sotechat.data.Mapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

/**
 * Käyttäjän tilan käsittelyyn liittyvä logiikka.
 */

public class StateService {


    /** Mapperilta voi esim. kysyä "mikä username on ID:llä x?". */
    private final Mapper mapperService;
    /** QueueService. */
    private final QueueService queueService;
    /** SubScribeEventHandler. */
    private final ApplicationListener subscribeEventListener;
    /** Channel where queue status is broadcasted. */
    private static final String QUEUE_BROADCAST_CHANNEL = "QBCC";
    // TODO: refaktoroi yhteen paikkaan QBCC määritykset


    /** Spring taikoo tässä konstruktorissa Singleton-instanssit palveluista.
     *
     * @param pMapper Olio, johon talletetaan tiedot käyttäjien id:istä
     * ja käyttäjänimistä, ja josta voidaan hakea esim. käyttäjänimi
     * käyttäjä-id:n perusteella.
     * @param pQueueService queueService
     * @param pSubscribeEventListener dfojfdoidfjo
     */
    @Autowired
    public StateService(
            final Mapper pMapper,
            final ApplicationListener pSubscribeEventListener,
            final QueueService pQueueService
    ) {
        this.mapperService = pMapper;
        this.subscribeEventListener = pSubscribeEventListener;
        this.queueService = pQueueService;
    }

    /** Logiikka miten vastataan customerClientin state requestiin.
     * @param session session
     * @return UserStateResponse
     */
    public final UserStateResponse respondToUserStateRequest(
            final HttpSession session
    ) {
        /** Varmistetaan, että sessionissa on asianmukaiset attribuutit. */
        Principal professional = null;
        updateSessionAttributes(session, professional);

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
     * @param session session
     * @return UserStateResponse
     */
    public final ProStateResponse respondToProStateRequest(
            final HttpSession session,
            final Principal professional
    ) {
        /** Varmistetaan, että sessionissa on asianmukaiset attribuutit. */
        updateSessionAttributes(session, professional);

        /** Kaivetaan sessionista tiedot muuttujiin. */
        String state = get(session, "state");
        String username = get(session, "username");
        String userId = get(session, "userId");
        String qbcc = QUEUE_BROADCAST_CHANNEL;
        String online = "true"; // TODO
        String channelIds = get(session, "channelIds");
        /** Note: ammattilaisella kaikki attribuutit relevantteja aina. */

        /** Paketoidaan muuttujat StateResponseen, joka käännetään JSONiksi. */
        //return new ProStateResponse(
        //        state, username, userId, qbcc, online, channelIds);
    }


    /** Logiikka mitä tehdään, kun tulee pyyntö liittyä pooliin (jonoon).
     * @param request req
     * @return ret
     * @throws IOException sdfsdf
     */
    public final String respondToJoinPoolRequest(
            final HttpServletRequest request
            ) throws IOException
    {
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
        session.setAttribute("state", "pool");
        /** JSON-muodossa, jotta AngularJS osaa ohjata success-metodille. */
        return "{\"content\":\"OK, please request new state now.\"}";
    }


    /** Metodi päivittää tarvittaessa session-attribuuttien state,
     * userId ja username vastaamaan ajanmukaisia arvoja.
     * @param session session
     * @param professional professional
     */
    public final void updateSessionAttributes(
            final HttpSession session,
            final Principal professional) {

        /** Kaivetaan username ja id sessio-attribuuteista. */
        Object username = session.getAttribute("username");
        Object userId = session.getAttribute("userId");

        /** Päivitetään muuttujat, jos tarpeellista. */
        if (professional != null) {
            /* Jos client on autentikoitunut ammattilaiseksi */
            username = professional.getName();
            userId = mapperService.getIdFromRegisteredName(username.toString());
            session.setAttribute("state", "notRelevantForProfessional");
            session.setAttribute("category", "notRelevantForProfessional");
            session.setAttribute("channelId", "DEV_CHANNEL"); // TODO
        } else if (get(session, "username").isEmpty()) {
            /* Uusi käyttäjä */
            username = "Anon";
            userId = mapperService.generateNewId();
            session.setAttribute("state", "start");
            session.setAttribute("category", "DRUGS"); //TODO

            /** Oikea kanavaID annetaan vasta nimen/aloitusviestin jälkeen. */
            String channelNotRelevantYet = mapperService.generateNewId();
            session.setAttribute("channelId", channelNotRelevantYet);
            /** Random kanava failsafena, jos jonkin virheen vuoksi
             * käyttäjät päätyisivätkin sinne keskustelemaan,
             * tyhjä kanava on parempi kuin kasa trolleja. */
        }

        /** Liitetään muuttujien tieto sessioon (monesti aiemman päälle). */
        session.setAttribute("username", username);
        session.setAttribute("userId", userId);

        /** Kirjataan tiedot mapperiin (monesti aiemman päälle). */
        mapperService.mapUsernameToId(userId.toString(), username.toString());
    }


    /** Esim: get(session, "username") -> "Matti".
     * Toistoa oli niin paljon, että eriytettiin omaksi metodiksi.
     * Palauttaa nullin sijaan tyhjän Stringin, jotta käsittely helpottuisi.
     * @param session HttpSession-objekti
     * @param attributeName Avain haettavalle attribuutille
     * @return Haettavan attribuutin arvo Stringinä
     */
    public static String get(
            final HttpSession session,
            final String attributeName)
    {
        if (session == null) {
            return "";
        }
        Object value = session.getAttribute(attributeName);
        if (value == null) {
            return "";
        }
        return value.toString();
    }

}
