package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import sotechat.data.Mapper;
import sotechat.service.ConversationService;
import sotechat.service.MessageService;
import sotechat.domain.Message;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;
import sotechat.service.ChatMessageService;

/** Reitittaa chattiin kirjoitetut viestit.
 */
@RestController
public class ChatController {

    /** Tarjoaa logiikan. */
    private final ChatMessageService chatMessageService;

    /** Viestien tietokantaan tallentamiseen */
    private final MessageService messageService;

    /** Viestien tallentaminen keskusteluun tietokannadssa */
    private final ConversationService conversationService;

    /** Konstruktori.
     * @param pChatService chatService
     */
    @Autowired
    public ChatController(
            final ChatMessageService pChatService,
            final MessageService pMessageService,
            final ConversationService pConversationService
    ) {
        this.chatMessageService = pChatService;
        this.messageService = pMessageService;
        this.conversationService = pConversationService;
    }

    /** Reitittaa chattiin kirjoitetut viestit ChatMessageServicelle,
     * joka palauttaa meille viestin kanavalle lahetettavassa muodossa
     * - tai null, jos viesti hylataan eika sita valiteta kanavalle.
     * MessageMapping annotaatiossa polku *palvelimelle* saapuviin viesteihin.
     *
     * @param msgToServer Asiakasohjelman JSON-muodossa lahettama viesti,
     *                    joka on paketoitu MsgToServer-olion sisalle.
     * @param accessor Haetaan session-tiedot taalta.
     * @return Palautusarvoa ei kayteta kuten yleensa, vaan SendTo-
     *         annotaatiossa on polku *clienteille* lahetettaviin viesteihin.
     *         Spring-magialla lahetetaan viesti kaikille kanavaan
     *         subscribanneille clienteille JSONina.
     * @throws Exception mika poikkeus?
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
