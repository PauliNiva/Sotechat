package sotechat.controller;

import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.WebSocketSession;
import sotechat.JoinResponse;
import sotechat.MsgToClient;
import sotechat.MsgToServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

/**
 * Controlleri, joka käsittelee serverin puolella
 * chat-liikenteen clienttien kanssa.
 * Kommenteissa "Mappays" viittaa siihen,
 * mitä serveri tekee, kun johonkin
 * tiettyyn polkuun tulee pyyntö.
 */
@RestController
public class ChatController {

    /** Alla metodi, joka käsittelee /toServer/{channelIid}
     * -polun kautta tulleet clientin viestit,
     * ja lähettää clientille vastauksen
     * polussa /toClient/{channelId}
     *
     * @param msgToServer Asiakasohjelman JSON-muodossa lähettämä viesti,
     *                    joka on paketoitu MsgToServer-olion sisälle.
     * @return Palautusarvoa ei käytetä kuten yleensä, vaan
     *         @SendTo -annotaatio saa Spring lähettämään
     *         palautusarvona määritellyn olion lähetettäväksi
     *         kaikille kanavalle subscribanneille henkilöille JSONina.
     *         TODO: Parempi kuvaus viestin välittämisestä.
     *
     * @throws Exception TODO: Selvitä mikä poikkeus.
     */
    @MessageMapping("/toServer/{id}")
    @SendTo("/toClient/{id}")
    public final MsgToClient routeMessage(
            final MsgToServer msgToServer) throws Exception {git
        // TODO: ID-to-name mappaykset Redisin kautta?
        String timeStamp = new DateTime().toString();
        // timeStamp täytyy antaa tässä muodossa AngularJS:n käsittelyyn.
        return new MsgToClient("" + msgToServer.getUserId(), msgToServer.getChannelId(),
                    timeStamp, msgToServer.getContent());
    }

    /** TODO: Kirjoita javadoc uusiks. Vanhaa tietoa.
     * Kun client menee sivulle index.html, tiedostoon upotettu
     * JavaScript tekee erillisen GET-pyynnön polkuun /join.
     * Tällä pyynnöllä client ilmaisee haluavansa chattiin.
     * Alla oleva metodi mappaa pyynnöt polkuun /join antamalla
     * käyttäjälle julkisen käyttäjänimen, salaisen käyttäjäID:n
     * sekä salaisen kanavaID:n (kehitysvaiheessa lähetetään
     * kaikki samalle kanavalle DEV_CHANNEL).
     * @return Palautusarvo lähetetään JSONina clientille.
     * @throws Exception TODO: Selvitä mikä poikkeus.
     */
    @RequestMapping("/join")
    public final JoinResponse returnJoinResponse(HttpServletRequest req, Principal professional) throws Exception {
        HttpSession session = req.getSession();

        /* If client is authenticated as a professional,
         * always write over session attributes. */
        if (professional != null) {
            session.setAttribute("username", professional.getName());
            session.setAttribute("userId", "Hoitajan_ID");
            // TODO: hae hoitajan ID nimen perusteella (redis?)
        }

        /* If session attributes still not known,
         * we have a new anonymous user. */
        if (session.getAttribute("username") == null) {
            String newUserId = ""+new Random().nextInt(Integer.MAX_VALUE);
            // TODO: ID-to-name mappings (redis?), no duplicate IDs.
            session.setAttribute("username", "Anon");
            session.setAttribute("userId", newUserId);
        }
        String username = session.getAttribute("username").toString();
        String userId = session.getAttribute("userId").toString();
        return new JoinResponse(username, userId, "DEV_CHANNEL");
    }

    /**
     * Alla mäppäys hoitajan hallintasivulle /pro.
     * TODO: Selvitä miten polkuja mapataan staattisiin resursseihin.
     * @return Palautetaan pyytajalle hallintasivu.
     * @throws Exception
     */
    @RequestMapping("/pro")
    public final String naytaHallintaSivu() throws Exception {
        return "Tänne tulisi hoitajan näkymä";
    }

}

