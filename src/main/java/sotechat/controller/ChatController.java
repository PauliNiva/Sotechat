package sotechat.controller;

import org.joda.time.DateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sotechat.JoinResponse;
import sotechat.MsgToClient;
import sotechat.MsgToServer;

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

    /**
     * Alla metodi, joka käsittelee /toServer/{channelIid}
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
    public final MsgToClient routeMessage(final MsgToServer msgToServer) throws Exception {
        String username = "Anon";
        // TODO: ID-to-name mappaykset Redisin kautta?
        String timeStamp = new DateTime().toString();
        // timeStamp täytyy antaa tässä muodossa AngularJS:n käsittelyyn.
        return new MsgToClient(username, msgToServer.getChannelId(),
                    timeStamp, msgToServer.getContent());
    }

    /**
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
    public final JoinResponse returnJoinResponse() throws Exception {
        Random rand = new Random();
        String username = "Anon";
        String userId = "" + rand.nextInt(Integer.MAX_VALUE);
        String channel = "DEV_CHANNEL";
        return new JoinResponse(username, userId, channel);
    }

    /**
     * Alla mäppäys hoitajan hallintasivulle /pro.
     * TODO: Selvitä miten polkuja mapataan staattisiin resursseihin.
     * @return
     * @throws Exception
     */
    @RequestMapping("/pro")
    public final String naytaHallintaSivu() throws Exception {
        return "Tänne tulisi hoitajan näkymä";
    }

}

