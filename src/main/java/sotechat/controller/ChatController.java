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
 */
@RestController
public class ChatController {

    /**
     * Metodi, joka käsittelee /toServer/{id}-polun kautta tulleet
     * asiakasohjelman viestit, ja lähettää asiakasohjelmalle vastauksen.
     *
     * @param msg Asiakasohjelman lähettämä viesti, jonka sisältö on
     *                paketoitu MsgToServer-olion sisälle.
     * @return Palauttaa MsgToClient-olion, joka on palvelimen lähettämä viesti
     * asiakasohjelmalle. Olion sisältö muokataan JSON-muotoon Springin
     * Jackson-kirjaston avulla ennen kuin asiakasohjelma vastaanottaa viestin.
     * @throws Exception TODO: Selvitä mikä
     */

    /** Ohjataan kehitysvaiheessa kaikki samalle kanavalle. */
    public static final int DEV_CHANNEL = 666;

    @MessageMapping("/toServer/{id}")
    @SendTo("/toClient/{id}")
    public final MsgToClient greeting(final MsgToServer msg) throws Exception {
        String username = "Anon";
        String timeStamp = new DateTime().toString();
        return new MsgToClient(username, msg.getChannelId(),
                    timeStamp, msg.getContent());
    }

    @RequestMapping("/join")
    public final JoinResponse returnJoinResponse() throws Exception {
        Random rand = new Random();
        String username = "Anon";
        String userId = "" + rand.nextInt(Integer.MAX_VALUE);
        String channel = Integer.toString(DEV_CHANNEL);
        return new JoinResponse(username, userId, channel);
    }

    @RequestMapping("/pro")
    public final String naytaHallintaSivu() throws Exception {
        return "Tänne tulisi hoitajan näkymä";
    }

}

