package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;
import sotechat.service.ChatMessageService;

/** Reitittaa chattiin kirjoitetut viestit.
 */
@RestController
public class ChatController {

    /** Tarjoaa logiikan. */
    private final ChatMessageService chatMessageService;

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
            final ChatMessageService pChatService
    ) {
        this.chatMessageService = pChatService;
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
