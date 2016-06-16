package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import sotechat.data.ChatLogger;
import sotechat.service.ValidatorService;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

/** Reitittaa chattiin kirjoitetut viestit.
 */
@RestController
public class ChatController {

    /** Chat Logger. */
    private final ChatLogger chatLogger;

    /** Validator Service. */
    private final ValidatorService validatorService;

    /** Konstruktori.
     * @param pChatLogger chatLogger
     * @param pValidatorService validatorServie
     */
    @Autowired
    public ChatController(
            final ChatLogger pChatLogger,
            final ValidatorService pValidatorService
    ) {
        this.chatLogger = pChatLogger;
        this.validatorService = pValidatorService;
    }

    /** Reitittaa chattiin kirjoitetut viestit muokattuna
     * - tai null, jos viesti hylataan eika sita valiteta kanavalle.
     *
     * @param msgToServer Asiakasohjelman JSON-muodossa lahettama viesti,
     *                    joka on paketoitu MsgToServer-olion sisalle.
     * @param accessor Haetaan session-tiedot taalta.
     * @return Palautusarvoa ei kayteta kuten yleensa, vaan SendTo-
     *         annotaatiossa on polku *clienteille* lahetettaviin viesteihin.
     *         Spring-magialla lahetetaan viesti kaikille kanavaan
     *         subscribanneille clienteille JSONina. MessageMapping
     *         annotaatiossa polku *palvelimelle* saapuviin viesteihin.
     * @throws Exception mika poikkeus?
     */
    @MessageMapping("/toServer/chat/{channelId}")
    @SendTo("/toClient/chat/{channelId}")
    public final MsgToClient routeMessage(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor accessor
            ) throws Exception {

        if (validatorService.isMessageFraudulent(msgToServer, accessor)) {
            /** Hakkerointiyritys? */
            return null;
        }
        /** Viesti ok, kirjataan se ylos. */
        MsgToClient msgToChannel = chatLogger.logNewMessage(msgToServer);
        /** Lahetetaan viesti kanavalle muokatussa muodossa. */
        return msgToChannel;
    }

}
