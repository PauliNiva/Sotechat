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
     * @param acc Haetaan session-tiedot taalta.
     * @return Palautusarvoa ei kayteta kuten yleensa, vaan SendTo-
     *         annotaatiossa on polku *clienteille* lahetettaviin viesteihin.
     *         Spring valittaa viestin kaikille kanavaan
     *         subscribanneille clienteille JSONina. MessageMapping
     *         annotaatiossa polku *palvelimelle* saapuviin viesteihin.
     */
    @MessageMapping("/toServer/chat/{channelId}")
    @SendTo("/toClient/chat/{channelId}")
    public final MsgToClient routeMessage(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor acc
    ) {

        String error = validatorService.isMessageFraudulent(msgToServer, acc);
        if (!error.isEmpty()) {
            System.out.println("Jokin viesti hylattiin syysta: " + error);
            return null;
        }
        /** Viesti ok, kirjataan se ylos ja valitetaan muokattuna kanavalle. */
        MsgToClient msgToClients = chatLogger.logNewMessage(msgToServer);
        System.out.println("MSG TO CLIENTS " + msgToClients);
        return msgToClients;
    }

}
