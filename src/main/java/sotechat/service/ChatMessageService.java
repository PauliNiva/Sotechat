package sotechat.service;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import sotechat.data.ChatLogger;
import sotechat.domain.Message;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;
import sotechat.data.Mapper;
import sotechat.domainService.ConversationService;
import sotechat.domainService.MessageService;

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

    private final DatabaseService databaseService;

    /**
     * Constructor autowires mapper.
     * @param pMapper mapper
     * @param pChatLogger chatLogger
     */
    @Autowired
    public ChatMessageService(
            final Mapper pMapper,
            final ChatLogger pChatLogger,
            final DatabaseService pDatabaseService
    ) {
        this.mapper = pMapper;
        this.chatLogger = pChatLogger;
        this.databaseService = pDatabaseService;
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

        /** Selvitetaan kayttajanimi annetun userId:n perusteella. */
        String userId = msgToServer.getUserId();

        String channelId = msgToServer.getChannelId();

        String content = msgToServer.getContent();

        String username = mapper.getUsernameFromId(userId);

        //TODO: tarkista, etta user on subscribannut kanavalle.

        if (validUserId(userId, accessor)) {
            return prepareMessage(username, channelId, content);
        }
        else return null;
    }

    /**
     * Muokataan viestistä clientille sopivan muotoinen ja lisätään siihen
     * aikaleima. Talletetaan viesti lokeihin sekä tietokanataan. Palautetaan
     * clientille sopivan muotoinen viesti.
     * @param username viestin lähettäjän käyttäjänimi
     * @param channelId viestin kanavan id
     * @param content viestin sisältö
     * @return MsgToClient muotoinen viesti -olio
     * @throws Exception
     */
    private MsgToClient prepareMessage(String username,
                                        String channelId, String content)
                                        throws Exception {

        String timeStampForClient = new DateTime().toString();
        Date timeForDB = new Date();

        MsgToClient msg = new MsgToClient(username, channelId,
                timeStampForClient, content);

        /** Tallennetaan viesti lokeihin. */
        chatLogger.log(msg);

        /**tallennetaan viesti tietokantaan. */
        databaseService.saveToDatabase(username, content, timeForDB, channelId);

        /** MsgToClient paketoidaan JSONiksi ja lahetetaan WebSocketilla. */
        return msg;
    }

    /**
     * Varmistetaan että kayttajan id on validi, että se kuuluu
     * ammattilaiselle, joka on kirjautunut ja että se on sama kuin
     * viestiin merkitty lahettajan id
     * @param userId
     * @param accessor
     * @return
     */
    private final boolean validUserId(String userId,
                                      SimpMessageHeaderAccessor accessor) {
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
        return true;
    }

}
