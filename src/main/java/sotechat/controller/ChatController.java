package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import sotechat.MsgToClient;
import sotechat.MsgToServer;
import sotechat.data.Mapper;
import sotechat.service.ChatMessageService;
import sotechat.service.QueueService;
import sotechat.data.SessionRepo;

/** Reitittaa chattiin kirjoitetut viestit.
 */
@RestController
public class ChatController {

    /** Tarjoaa logiikan. */
    private final ChatMessageService chatMessageService;

    /** Konstruktori, jolla Spring taikoo singleton-instanssin servicesta.
     * @param pChatService Chat Message Service.
     */
    @Autowired
    public ChatController(
            final ChatMessageService pChatService
    ) {
        this.chatMessageService = pChatService;
    }

    /** Reitittaa chattiin kirjoitetut viestit ChatMessageServicelle,
     * joka palauttaa meille viestin kanavalle lähetettävässä muodossa
     * - tai null, jos viesti hylätään eikä sitä välitetä kanavalle.
     * MessageMapping annotaatiossa polku *palvelimelle* saapuviin viesteihin.
     *
     * @param msgToServer Asiakasohjelman JSON-muodossa lähettämä viesti,
     *                    joka on paketoitu MsgToServer-olion sisälle.
     * @param accessor Haetaan session-tiedot täältä.
     * @return Palautusarvoa ei käytetä kuten yleensä, vaan SendTo-
     *         annotaatiossa on polku *clienteille* lähetettäviin viesteihin.
     *         Spring-magialla lähetetään viesti kaikille kanavaan
     *         subscribanneille clienteille JSONina.
     * @throws Exception mikä poikkeus?
     */
    @MessageMapping("/toServer/chat/{channelId}")
    @SendTo("/toClient/chat/{channelId}")
    public final MsgToClient routeMessage(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor accessor
            ) throws Exception {

        return chatMessageService.processMessage(
                msgToServer,
                accessor
        );
    }

}

