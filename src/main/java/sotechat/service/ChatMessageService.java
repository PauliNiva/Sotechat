package sotechat.service;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import sotechat.data.ChatLogger;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;
import sotechat.data.Mapper;

/**
 * Logiikka chat-viestien kasittelyyn.
 */
@Service
public class ChatMessageService {

    /** Mapperilta voi esim. kysya "mika username on ID:lla x?". */
    private final Mapper mapper;

    /** ChatLogger. */
    private final ChatLogger chatLogger;

    /**
     * Constructor autowires mapper.
     * @param pMapper mapper.
     */
    @Autowired
    public ChatMessageService(
            final Mapper pMapper,
            final ChatLogger pChatLogger
    ) {
        this.mapper = pMapper;
        this.chatLogger = pChatLogger;
    }


    /** Logiikka saapuvien chat-viestien kasittelyyn.
     * Palauttaa viestin muodossa, joka voidaan lahettaa kanavalla olijoille.
     * @param msgToServer saapuva viesti
     * @param accessor accessor
     * @return msgToClient eli lahteva viesti
     */
    public MsgToClient processMessage(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor accessor
    ) {
        /** Annetaan timeStamp juuri tassa muodossa AngularJS:aa varten. */
        String timeStamp = new DateTime().toString();

        /** Selvitetaan kayttajanimi annetun userId:n perusteella. */
        String userId = msgToServer.getUserId();
        if (!mapper.isUserIdMapped(userId)) {
            /** Kelvoton ID, hylataan viesti. */
            return null;
        }
        if (mapper.isUserProfessional(userId)) {
            /** ID kuuluu ammattilaiselle, varmistetaan etta on kirjautunut. */

            if (accessor.getUser() == null) {
                /** Ei kirjautunut, hylataan viesti. */
                return null;
            }
            String username = accessor.getUser().getName();
            String authId = mapper.getIdFromRegisteredName(username);
            if (!userId.equals(authId)) {
                /** Kirjautunut ID eri kuin viestiin merkitty lahettajan ID. */
                return null;
            }

        }
        String username = mapper.getUsernameFromId(userId);

        //TODO: tarkista, etta user on subscribannut kanavalle.

        MsgToClient msg = new MsgToClient(username, msgToServer.getChannelId(),
                timeStamp, msgToServer.getContent());

        /** Tallennetaan viesti lokeihin. */
        chatLogger.log(msg);

        /** MsgToClient paketoidaan JSONiksi ja lahetetaan WebSocketilla. */
        return msg;
    }

}
