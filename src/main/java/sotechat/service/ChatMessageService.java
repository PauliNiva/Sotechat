package sotechat.service;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;
import sotechat.data.Mapper;

/**
 * Logiikka chat-viestien käsittelyyn.
 */
@Service
public class ChatMessageService {

    /** Mapperilta voi esim. kysyä "mikä username on ID:llä x?". */
    private final Mapper mapper;

    /**
     * Constructor autowires mapper.
     * @param pMapper mapper.
     */
    @Autowired
    public ChatMessageService(
            final Mapper pMapper
    ) {
        this.mapper = pMapper;
    }


    /** Logiikka saapuvien chat-viestien käsittelyyn.
     * Palauttaa viestin muodossa, joka voidaan lähettää kanavalla olijoille.
     * @param msgToServer saapuva viesti
     * @param accessor accessor
     * @return msgToClient eli lähtevä viesti
     */
    public MsgToClient processMessage(
            final MsgToServer msgToServer,
            final SimpMessageHeaderAccessor accessor
    ) {
        /** Annetaan timeStamp juuri tässä muodossa AngularJS:ää varten. */
        String timeStamp = new DateTime().toString();

        /** Selvitetään käyttäjänimi annetun userId:n perusteella. */
        String userId = msgToServer.getUserId();
        if (!mapper.isUserIdMapped(userId)) {
            /** Kelvoton ID, hylätään viesti. */
            return null;
        }
        if (mapper.isUserProfessional(userId)) {
            /** ID kuuluu ammattilaiselle, varmistetaan että on kirjautunut. */

            if (accessor.getUser() == null) {
                /** Ei kirjautunut, hylätään viesti. */
                return null;
            }
            String username = accessor.getUser().getName();
            String authId = mapper.getIdFromRegisteredName(username);
            if (!userId.equals(authId)) {
                /** Kirjautunut ID eri kuin viestiin merkitty lähettäjän ID. */
                return null;
            }

        }
        String username = mapper.getUsernameFromId(userId);

        /** MsgToClient paketoidaan JSONiksi ja lähetetään WebSocketilla. */
        return new MsgToClient(username, msgToServer.getChannelId(),
                timeStamp, msgToServer.getContent());
    }

}
