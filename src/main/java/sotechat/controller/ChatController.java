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
 * Kontrolleriluokka, joka käsittelee asiakasohjelman ja palvelimen väliset
 * chattiin liittyvät viestit.
 *
 * @since 19.5.2016
 */
@RestController
public class ChatController {

    private static final int UPPER_BOUND = 500;

    /**
     * Metodi, joka käsittelee /toServer/{id}-polun kautta tulleet
     * asiakasohjelman viestit, ja lähettää asiakasohjelmalle vastauksen.
     *
     * @param msg Asiakasohjelman lähettämä viesti, jonka sisältö on
     *                paketoitu MsgToServer-olion sisälle.
     * @return Palauttaa MsgToClient-olion, joka on palvelimen lähettämä viesti
     * asiakasohjelmalle. Olion sisältö muokataan JSON-muotoon Springin
     * Jackson-kirjaston avulla ennen kuin asiakasohjelma vastaanottaa viestin.
     * @throws Exception MIKÄ EXCEPTION!!!!!!!!!!!! TODO
     */
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
        String username = "Anon" + rand.nextInt(UPPER_BOUND);
        String userId = "" + rand.nextInt(UPPER_BOUND * 2);
        String channel = "" + rand.nextInt(2);
        return new JoinResponse(username, userId, channel);
    }
}

