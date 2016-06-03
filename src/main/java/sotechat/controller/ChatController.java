package sotechat.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.security.Principal;
import java.util.Map;

import sotechat.JoinResponse;
import sotechat.MsgToClient;
import sotechat.MsgToServer;
import sotechat.StateResponse;
import sotechat.data.Mapper;
import sotechat.service.QueueService;

/** Controlleri, joka käsittelee serverin puolella
 * chat-liikenteen clienttien kanssa.
 * Kommenteissa "Mappays" viittaa siihen,
 * mitä serveri tekee, kun johonkin
 * tiettyyn polkuun tulee pyyntö.
 */
@RestController
public class ChatController {

    /** Mapperilta voi esim. kysyä "mikä username on ID:llä x?". */
    private final Mapper mapper;

    /** Hoitaa jonon palvelut */
    @Autowired
    private final QueueService queueService;

    /** SubScribeEventHandler. */
    private final ApplicationListener subscribeEventHandler;

    /** Spring taikoo tässä Singleton-instanssin mapperista.
     *
     * @param pMapper Olio johon talletetaan tiedot käyttäjien id:istä
     * ja käyttäjänimistä, ja josta voidaan hakea esim. käyttäjänimi
     * käyttäjä-id:n perusteella.
     * @param queueService hoitaa jonon palvelut
     */
    @Autowired
    public ChatController(
            final Mapper pMapper,
            final ApplicationListener subscribeEventHandler,
            final QueueService queueService) {
        this.mapper = pMapper;
        this.subscribeEventHandler = subscribeEventHandler;
        this.queueService = queueService;
    }

    /** Alla metodi, joka käsittelee /toServer/{channelIid}
     * -polun kautta tulleet clientin WebSocket-viestit,
     * ja lähettää clientille vastauksen polussa /toClient/{channelId}.
     *
     * @param msgToServer Asiakasohjelman JSON-muodossa lähettämä viesti,
     *                    joka on paketoitu MsgToServer-olion sisälle.
     * @param accessor Haetaan session-tiedot täältä.
     * @return Palautusarvoa ei käytetä kuten yleensä, vaan
     *         @SendTo -annotaatio saa Spring lähettämään
     *         palautusarvona määritellyn olion lähetettäväksi
     *         kaikille kanavalle subscribanneille henkilöille JSONina.
     * @throws Exception mikä poikkeus?
     */
    @MessageMapping("/toServer/{channelId}")
    @SendTo("/toClient/{channelId}")
    public final MsgToClient routeMessage(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor accessor
    ) throws Exception {


        /** Annetaan timeStamp juuri tässä muodossa AngularJS:ää varten. */
        String timeStamp = new DateTime().toString();

        /** Selvitetään käyttäjänimi annetun userId:n perusteella. */
        String userId = msgToServer.getUserId();
        if (!mapper.isUserIdMapped(userId)) {
            /** Kelvoton ID, hylätään viesti. */
            return null;
        }
        if (mapper.isUserProfessional(userId)) {
            /** ID kuuluu ammattilaiselle, varmistetaan että on kirjautunut. */

            if (accessor.getUser() == null) {
                /** Ei kirjautunut, hylätään viesti. */
                return null;
            }
            String username = accessor.getUser().getName();
            String authId = mapper.getIdFromRegisteredName(username);
            if (!userId.equals(authId)) {
                /** Kirjautunut ID eri kuin viestiin merkitty lähettäjän ID. */
                return null;
            }

        }
        String username = mapper.getUsernameFromId(userId);

        /** MsgToClient paketoidaan JSONiksi ja lähetetään WebSocketilla. */
        return new MsgToClient(username, msgToServer.getChannelId(),
                    timeStamp, msgToServer.getContent());
    }

    /** Kun client haluaa pyytää tilan (mm. sivun latauksen yhteydessä).
     * @param req req
     * @param professional pro
     * @return mitä vastataan clientille
     * @throws Exception mikä poikkeus
     */
    @RequestMapping(value = "/state", method = RequestMethod.GET)
    public final StateResponse returnStateResponse(
            final HttpServletRequest req, final Principal professional)
            throws Exception {
        HttpSession session = req.getSession();

        System.out.println("     id(state) = " + session.getId());

        /** Varmistetaan, että sessionissa on asianmukainen userId,username. */
        updateSessionAttributes(session, professional);

        /** Kaivetaan sessionista tiedot muuttujiin. */
        String state = session.getAttribute("state").toString();
        String username = session.getAttribute("username").toString();
        String userId = session.getAttribute("userId").toString();
        String category = session.getAttribute("category").toString();
        String channel = session.getAttribute("channelId").toString();
        // TODO: Jos hoitaja -> lista channelId:tä ?

        /** Paketoidaan muuttujat StateResponseen, joka käännetään JSONiksi. */
        return new StateResponse(state, username, userId, category, channel);
    }

    /** Kun client lähettää avausviestin ja haluaa liittyä pooliin.
     * @param request request
     * @param professional professional
     * @return mitä vastataan clientille
     * @throws Exception mikä poikkeus
     */
    @RequestMapping(value = "/joinPool", method = RequestMethod.POST)
    public final String respondToJoinPoolRequest(
            final HttpServletRequest request,
            final Principal professional
    )throws Exception {
        HttpSession session = request.getSession();

        /** Tehdään JSON-objekti clientin lähettämästä JSONista. */
        String jsonString = request.getReader().readLine();
        JsonParser parser = new JsonParser();
        JsonObject payload = parser.parse(jsonString).getAsJsonObject();
        String username = payload.get("username").getAsString();


        System.out.println("     id(joinPool) = " + session.getId() + " , username = " + username);



        if (!stateToString(session).equals("start")) {
            /** Ei JSON-muodossa, jotta AngularJS osaa ohjata fail-metodille. */
            System.out.println(stateToString(session));
            return "Denied join pool request due to bad state.";
        }
        if (false) {
            // TODO: validoi username.
            /** Ei JSON-muodossa, jotta AngularJS osaa ohjata fail-metodille. */
            return "Denied join pool request due to reserved username.";
        }


        String userId = session.getAttribute("userId").toString();
        session.setAttribute("username", username);
        mapper.mapUsernameToId(userId, username);

        session.setAttribute("state", "pool");
        /** JSON-muodossa, jotta AngularJS osaa ohjata success-metodille. */
        return "{\"content\":\"OK, please request new state now.\"}";

    }

    /** Palauttaa String-esityksen sessioon liitetystä tilasta. Voi olla "".
     * @param session session
     * @return tila Stringinä, nullin sijaan tyhjä Stringi.
     */
    public static String stateToString(final HttpSession session) {
        if (session == null) return "";
        return session.getAttribute("state").toString();
    }

    /** Kun hoitaja ottaa jonosta chatin, tänne pitäisi tulla signaali.
     * @param accessor accessor
     * @return Palautusarvo kuljetetaan "jonotuskanavan" kautta jonottajalle.
     * @throws Exception mikä poikkeus
     */
    @MessageMapping("/toServer/queue/{channelId}")
    @SendTo("/toClient/queue/{channelId}")
    public final String popClientFromQueue(
            final SimpMessageHeaderAccessor accessor,
            @DestinationVariable String channelId
    ) throws Exception {
        //upgradeSubscribersToChat(channelId);
        // TODO: Poista jonosta
        System.out.println("Opening channel Id: " + channelId);
        return "{\"content\":\"channel activated. request new state now.\"}";
    }

    /** Kun client menee sivulle index.html, tiedostoon upotettu
     * JavaScript tekee erillisen GET-pyynnön polkuun /join.
     * Tällä pyynnöllä client ilmaisee haluavansa chattiin.
     * Alla oleva metodi mappaa pyynnöt polkuun /join
     * ja palauttaa käyttäjälle JSONina usernamen, userId:n
     * ja kanavaId:n (kaikki samalle kanavalle DEV_CHANNEL).
     * @return Palautusarvo lähetetään JSONina clientille.
     * @throws Exception mikä poikkeus?
     * @param req Http GET-pyyntö osoitteesee /join.
     * @param professional Kirjautuneelle käyttäjälle(hoitaja) luotu
     *                     istunto(session) kirjautumisen yhteydessä
     */
    @RequestMapping(value = "/join", method = RequestMethod.GET)
    public final JoinResponse returnJoinResponse(
            final HttpServletRequest req, final Principal professional)
            throws Exception {
        HttpSession session = req.getSession();

        System.out.println("     id(join) = " + session.getId());

        updateSessionAttributes(session, professional);
        String username = session.getAttribute("username").toString();
        String userId = session.getAttribute("userId").toString();

        /** Palautetaan JoinResponse, jonka Spring paketoi JSONiksi. */
        return new JoinResponse(username, userId, "DEV_CHANNEL");
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
            userId = mapper.getIdFromRegisteredName(username.toString());
            session.setAttribute("state", "notRelevantForProfessional");
            session.setAttribute("category", "notRelevantForProfessional");
            session.setAttribute("channelId", "DEV_CHANNEL"); // TODO
        } else if (session.getAttribute("username") == null) {
            /* Uusi käyttäjä */
            username = "Anon";
            userId = mapper.generateNewId();
            session.setAttribute("state", "start");
            session.setAttribute("category", "DRUGS"); //TODO

            /** Oikea kanavaID annetaan vasta nimen/aloitusviestin jälkeen. */
            String channelNotRelevantYet = mapper.generateNewId();
            session.setAttribute("channelId", channelNotRelevantYet);
            /** Random kanava failsafena, jos jonkin virheen vuoksi
             * käyttäjät päätyisivätkin sinne keskustelemaan,
             * tyhjä kanava on parempi kuin kasa trolleja. */
        }

        /** Liitetään muuttujien tieto sessioon (monesti aiemman päälle). */
        session.setAttribute("username", username);
        session.setAttribute("userId", userId);

        /** Kirjataan tiedot mapperiin (monesti aiemman päälle). */
        this.mapper.mapUsernameToId(userId.toString(), username.toString());
    }

}

