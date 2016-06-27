package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageBroker {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    //TODO: join ja leave noticet omiksi metodeiksi tahan luokkaan.

    public void sendClosedChannelNotice(String channelId) {
        String channelIdWithPath = "/toClient/chat/" + channelId;
        String closedChannelNotice = "{\"notice\":\"chat closed\"}";
        simpMessagingTemplate.convertAndSend(
                channelIdWithPath, closedChannelNotice);
    }

    public void convertAndSend(String path, Object content) {
        simpMessagingTemplate.convertAndSend(path, content);
    }
}
