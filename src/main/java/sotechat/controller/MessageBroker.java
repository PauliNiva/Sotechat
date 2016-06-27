package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageBroker {

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
}
