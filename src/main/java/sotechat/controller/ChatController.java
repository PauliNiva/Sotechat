package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

/** Reitittaa chattiin kirjoitetut viestit.
 */
@RestController
public class ChatController {

    /** Chat Logger. */
    private final ChatLogger chatLogger;

    /** Mapperi. */
    private final Mapper mapper;

    /** Konstruktori.
     * @param pChatLogger chatLogger
     * @param pMapper mapper
     */
    @Autowired
    public ChatController(
            final ChatLogger pChatLogger,
            final Mapper pMapper
    ) {
        this.chatLogger = pChatLogger;
        this.mapper = pMapper;
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

        if (fraudulentMessage(msgToServer, accessor)) {
            /** Hakkerointiyritys? */
            return null;
        }
        /** Viesti ok, kirjataan se ylos ja valitetaan eri muodossa. */
        MsgToClient msgToChannel = chatLogger.logNewMessage(msgToServer);
        return msgToChannel;
    }

    /** Onko viesti vaarennetty?
     * @param msgToServer msgToServer
     * @param accessor accessor
     * @return true jos sallitaan
     */
    private boolean fraudulentMessage(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor accessor
    ) {
        String userId = msgToServer.getUserId();
        if (!mapper.isUserIdMapped(userId)) {
            /** Kelvoton ID, hylataan viesti. */
            return true;
        }
        if (mapper.isUserProfessional(userId)) {
            /** ID kuuluu ammattilaiselle, varmistetaan etta on kirjautunut. */

            if (accessor.getUser() == null) {
                /** Ei kirjautunut, hylataan viesti. */
                return true;
            }
            String username = accessor.getUser().getName();
            String authId = mapper.getIdFromRegisteredName(username);
            if (!userId.equals(authId)) {
                /** Kirjautunut ID eri kuin viestiin merkitty lahettajan ID. */
                return true;
            }
        }

        // TODO:
        // tarkista subscribeEventListenerista, etta kirjoittaja
        // on subscribannut kanavalle.
        String channelId = msgToServer.getChannelId();
        String chatPrefix = "/toClient/chat/";
        String channelIdWithPath = chatPrefix + channelId;

        return false;
    }

}
