package sotechat.controller;

import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sotechat.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

import sotechat.data.Mapper;

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

    /** Spring taikoo tässä Singleton-instanssin mapperista.
     *
     * @param pMapper Olio johon talletetaan tiedot käyttäjien id:istä
     * ja käyttäjänimistä, ja josta voidaan hakea esim. käyttäjänimi
     * käyttäjä-id:n perusteella.
     */
    @Autowired
    public ChatController(final Mapper pMapper) {
        this.mapper = pMapper;
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

    /** Kun client lataa sivun ja haluaa pyytää tilan.
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
     * @param response response
     * @param professional professional
     * @return mitä vastataan clientille
     * @throws Exception mikä poikkeus
     */
    @RequestMapping(value = "/joinPool", method = RequestMethod.POST)
    public  final String returnStateResponse(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Principal professional)
            throws Exception {
        HttpSession session = request.getSession();

        if (!session.getAttribute("state").toString().equals("start")) {
            return "Denied, join pool request must come from start state.";
        }

        // validoi nimi
        // String userId = session.getAttribute("userId");
        // session.setAttribute("username", username);
        // mapper.mapUsernameToId(userId, username);


        session.setAttribute("state", "pool");

        /** */
        return "OK, please request new state now.";

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
        String username = session.getAttribute("username").toString();
        String userId = session.getAttribute("userId").toString();

        /** Päivitetään muuttujat, jos tarpeellista. */
        if (professional != null) {
            /* Jos client on autentikoitunut ammattilaiseksi */
            username = professional.getName();
            userId = mapper.getIdFromRegisteredName(username);
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
        this.mapper.mapUsernameToId(userId, username);
    }

    /**
     * Alla mäppäys hoitajan hallintasivulle /pro.
     * TODO: Selvitä miten polkuja mapataan staattisiin resursseihin.
     * @return Palautetaan autentikoituneelle clientille hallintasivu.
     * @throws Exception mikä poikkeus.
     */
  /**  @RequestMapping("/pro")
    public final String naytaHallintaSivu() throws Exception {
        return "Tänne tulisi hoitajan näkymä";
    }*/


}

