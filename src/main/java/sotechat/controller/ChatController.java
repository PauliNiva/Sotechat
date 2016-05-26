package sotechat.controller;

import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sotechat.JoinResponse;
import sotechat.MsgToClient;
import sotechat.MsgToServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;

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
            final MsgToServer msgToServer, SimpMessageHeaderAccessor headerAccessor) throws Exception {
  //      String username = "Anon";
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        HttpSession session = 
        for (int i = 0; i < 50; i++) {
            System.out.println("===============");
        }
        for (String s : attrs.keySet()) {
            System.out.println(s);
        }
    /*    HttpSession session = req.getSession();
        String username = session.getAttribute("username").toString();*/
        // TODO: ID-to-name mappaykset Redisin kautta?
        String timeStamp = new DateTime().toString();
        // timeStamp täytyy antaa tässä muodossa AngularJS:n käsittelyyn.
        return new MsgToClient("pekka", msgToServer.getChannelId(),
                    timeStamp, msgToServer.getContent());
    }

    /** Kun client menee sivulle index.html, tiedostoon upotettu
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
    public final JoinResponse returnJoinResponse(HttpServletRequest req) throws Exception {
        // TODO: session kommentointi
        HttpSession session = req.getSession();
        if (session.getAttribute("username") == null) {
            Random rand = new Random();
            String username = "Anon";
            String channel = "DEV_CHANNEL";
            session.setAttribute("username", username);
            session.setAttribute("channelId", channel);
        }
        return new JoinResponse(session.getAttribute("username").toString(), session.getAttribute("channelId").toString());
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

