package sotechat.controller;

import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sotechat.JoinResponse;
import sotechat.MsgToClient;
import sotechat.MsgToServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    @RequestMapping("/join")
    public final JoinResponse returnJoinResponse(
            final HttpServletRequest req, final Principal professional)
            throws Exception {

        HttpSession session = req.getSession();
        /** Kyseessä nimenomaan HTTP-session, ei WebSocket-session.
         * WebSocket-viestien mukana ei kulje HTTP-headereitä,
         * jonka vuoksi HTTP-sessionia ei voida käyttää userien
         * identifioimiseen, vaan käytetään userId:tä. */

        if (professional != null) {
            /* Jos client on autentikoitunut ammattilaiseksi,
             * kirjoitetaan sessio-attribuutit aina
             * (mahdollisten aiempien attribuuttien päälle). */
            String username = professional.getName();
            String userId = mapper.getIdFromRegisteredName(username);
            session.setAttribute("username", username);
            session.setAttribute("userId", userId);
        }

        if (session.getAttribute("username") == null) {
            /* Jos sessio-attribuutit ovat vieläkin tuntemattomat,
             * käyttäjä ei voi olla ammattilainen, joten
             * luodaan uusi userID anonymous-käyttäjälle ja
             * tallennetaan sessio-attribuutteihin. */
            String newUserId = mapper.generateNewId();
            session.setAttribute("username", "Anon");
            session.setAttribute("userId", newUserId);
        }

        /** Kaivetaan username ja id sessio-attribuuteista. */
        String username = session.getAttribute("username").toString();
        String userId = session.getAttribute("userId").toString();

        /** Kirjataan ne mapperiin (vaikka ne monesti jo olivat siellä). */
        this.mapper.mapUsernameToId(userId, username);

        /** Palautetaan JoinResponse, jonka Spring paketoi JSONiksi. */
        return new JoinResponse(username, userId, "DEV_CHANNEL");
    }

    /**
     * Alla mäppäys hoitajan hallintasivulle /pro.
     * TODO: Selvitä miten polkuja mapataan staattisiin resursseihin.
     * @return Palautetaan autentikoituneelle clientille hallintasivu.
     * @throws Exception mikä poikkeus.
     */
    @RequestMapping("/pro")
    public final String naytaHallintaSivu() throws Exception {
        return "Tänne tulisi hoitajan näkymä";
    }

}

