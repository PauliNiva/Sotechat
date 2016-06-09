package sotechat.websocketService;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import sotechat.data.ChatLogger;
import sotechat.domain.Message;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;
import sotechat.data.Mapper;

import java.util.Date;

/**
 * Logiikka chat-viestien kasittelyyn.
 */
@Service
public class ChatMessageService {

    /** Mapperilta voi esim. kysya "mika username on ID:lla x?". */
    private final Mapper mapper;

    /** ChatLogger. */
    private final ChatLogger chatLogger;

    private final ConversationService conversationService;

    private final MessageService messageService;

    /**
     * Constructor autowires mapper.
     * @param pMapper mapper
     * @param pChatLogger chatLogger
     */
    @Autowired
    public ChatMessageService(
            final Mapper pMapper,
            final ChatLogger pChatLogger,
            final ConversationService pConvService,
            final MessageService pMessageServ
    ) {
        this.mapper = pMapper;
        this.chatLogger = pChatLogger;
        this.conversationService = pConvService;
        this.messageService = pMessageServ;
    }


    /** Logiikka saapuvien chat-viestien kasittelyyn.
     * Tallentaa viestin tietokantaan ja palauttaa viestin muodossa,
     * joka voidaan lahettaa kanavalla olijoille.
     * @param msgToServer saapuva viesti
     * @param accessor accessor
     * @return msgToClient eli lahteva viesti
     */
    public final synchronized MsgToClient processMessage(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor accessor
    ) throws Exception {

        Date time = new Date();

        /** Selvitetaan kayttajanimi annetun userId:n perusteella. */
        String userId = msgToServer.getUserId();

        String channelId = msgToServer.getChannelId();

        String content = msgToServer.getContent();

        //TODO: tarkista, etta user on subscribannut kanavalle.

        if (validUserId(userId, accessor)) {
            return preparedMessage(time, userId, channelId, content);
        }
        else return null;
    }

    private MsgToClient preparedMessage(Date time, String userId,
                                        String channelId, String content)
                                        throws Exception {

        String username = mapper.getUsernameFromId(userId);

        MsgToClient msg = new MsgToClient(username, channelId,
                time.toString(), content);

        /** Tallennetaan viesti lokeihin. */
        chatLogger.log(msg);

        /**tallennetaan viesti tietokanataan. */
        saveToDatabase(username, content, time, channelId);

        /** MsgToClient paketoidaan JSONiksi ja lahetetaan WebSocketilla. */
        return msg;
    }

    private final boolean validUserId(String userId, SimpMessageHeaderAccessor accessor){
        if (!mapper.isUserIdMapped(userId)) {
            /** Kelvoton ID, hylataan viesti. */
            return false;
        }
        if (mapper.isUserProfessional(userId)) {
            /** ID kuuluu ammattilaiselle, varmistetaan etta on kirjautunut. */

            if (accessor.getUser() == null) {
                /** Ei kirjautunut, hylataan viesti. */
                return false;
            }
            String username = accessor.getUser().getName();
            String authId = mapper.getIdFromRegisteredName(username);
            if (!userId.equals(authId)) {
                /** Kirjautunut ID eri kuin viestiin merkitty lahettajan ID. */
                return false;
            }

        }
    }

    private final void saveToDatabase(String username, String content,
                                     Date time, String channelId)
                                     throws Exception {
        Message message = new Message(username, content, time);
        messageService.addMessage(message);
        conversationService.addMessage(message, channelId);
    }

}
