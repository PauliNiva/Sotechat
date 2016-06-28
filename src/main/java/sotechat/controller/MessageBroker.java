package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Component
public class MessageBroker {

    @Autowired
    SessionRepo sessionRepo;

    @Autowired
    Mapper mapper;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    //TODO join ja leave noticet omiksi metodeiksi tahan luokkaan.

    public void sendClosedChannelNotice(final String channelId) {
        String channelIdWithPath = "/toClient/chat/" + channelId;
        String closedChannelNotice = "{\"notice\":\"chat closed\"}";
        simpMessagingTemplate.convertAndSend(
                channelIdWithPath, closedChannelNotice);
    }

    public void convertAndSend(final String path, final Object content) {
        simpMessagingTemplate.convertAndSend(path, content);
    }

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

    public void sendJoinNotices(
            Session session
    ) {
        String username = session.get("username");
        for (String channelId : session.getChannels()) {
            String channelIdWithPath = "/toClient/chat/" + channelId;
            sendJoinNotice(channelIdWithPath, username);
        }
    }

    public void sendLeaveNotices(
            Session session
    ) {
        String username = session.get("username");
        for (String channelId : session.getChannels()) {
            String channelIdWithPath = "/toClient/chat/" + channelId;
            sendLeaveNotice(channelIdWithPath, username);
        }
    }

    public void sendJoinNotice(
            String path,
            String username
    ) {
        String joinInfo = "{\"join\":\"" + username + "\"}";
        simpMessagingTemplate.convertAndSend(path, joinInfo);
    }

    public void sendLeaveNotice(
            String path,
            String username
    ) {
        String leaveInfo = "{\"leave\":\"" + username + "\"}";
        simpMessagingTemplate.convertAndSend(path, leaveInfo);
    }
}
