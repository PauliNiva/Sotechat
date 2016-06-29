package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.security.Principal;

import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

/**
 * Kanavalle lahetettavien viestien ja tilatietojen valittaminen.
 */
@Component
public class MessageBroker {

    /**
     * Sessioiden kasittely.
     */
    @Autowired
    SessionRepo sessionRepo;

    /**
     * Muistaa asioita kayttajiin liittyen.
     */
    @Autowired
    Mapper mapper;

    /**
     * Spring:in viestienvalittajaolio.
     */
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Lahettaa kanavalle tiedotuksen kanavan sulkemisesta.
     * @param channelId Kanavantunnus.
     */
    public void sendClosedChannelNotice(final String channelId) {
        String channelIdWithPath = "/toClient/chat/" + channelId;
        String closedChannelNotice = "{\"notice\":\"chat closed\"}";
        simpMessagingTemplate.convertAndSend(
                channelIdWithPath, closedChannelNotice);
    }

    /**
     * Lahettaa annetun polun tilaajille sisallon.
     * @param path Polku.
     * @param content Sisalto.
     */
    public void convertAndSend(final String path, final Object content) {
        simpMessagingTemplate.convertAndSend(path, content);
    }

    /**
     * Lahettaa kaikilla kanaville, joilla annettu ammattilaiskayttaja on,
     * tiedotteen, etta kayttaja on poistunut tai liittynyt.
     *
     * @param pro Authentikaatiotiedot.
     * @param online String true tai false.
     */
    public void sendJoinLeaveNotices(
            Principal pro,
            String online
    ) {
        String username = pro.getName();
        String userId = mapper.getIdFromRegisteredName(username);
        Session session = sessionRepo.getSessionFromUserId(userId);
        if (online.equals("true")) {
            sendJoinNotices(session);
        } else {
            sendLeaveNotices(session);
        }
    }

    /**
     * Lahettaa annetun session kanaville tieto liittymisesta.
     *
     * @param session Liittyja.
     */
    public void sendJoinNotices(
            Session session
    ) {
        String username = session.get("username");
        for (String channelId : session.getChannels()) {
            String channelIdWithPath = "/toClient/chat/" + channelId;
            sendJoinNotice(channelIdWithPath, username);
        }
    }

    /**
     * Lahettaa annetun session kanaville tieto poistuisesta.
     *
     * @param session Poistuja.
     */
    public void sendLeaveNotices(
            Session session
    ) {
        String username = session.get("username");
        for (String channelId : session.getChannels()) {
            String channelIdWithPath = "/toClient/chat/" + channelId;
            sendLeaveNotice(channelIdWithPath, username);
        }
    }

    /**
     * Lahettaa annetulle kanavalle tiedon kayttajan liittymisesta.
     *
     * @param path Polku.
     * @param username Liittyja.
     */
    public void sendJoinNotice(
            String path,
            String username
    ) {
        String joinInfo = "{\"join\":\"" + username + "\"}";
        simpMessagingTemplate.convertAndSend(path, joinInfo);
    }

    /**
     * Lahettaa annetulle kanavalle tiedon kayttajan poistumisesta.
     *
     * @param path Polku.
     * @param username Poistuja.
     */
    public void sendLeaveNotice(
            String path,
            String username
    ) {
        String leaveInfo = "{\"leave\":\"" + username + "\"}";
        simpMessagingTemplate.convertAndSend(path, leaveInfo);
    }

}
