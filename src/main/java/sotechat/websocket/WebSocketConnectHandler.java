package sotechat.websocket;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.*;

public class WebSocketConnectHandler<S>
        implements ApplicationListener<SessionConnectEvent> {

    private SimpMessageSendingOperations messagingTemplate;

    public WebSocketConnectHandler(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

   public void onApplicationEvent(SessionConnectEvent event) {
       Random rand = new Random();
       String username = "Anon";
       String userId = "" + rand.nextInt(Integer.MAX_VALUE);
       String name = username + userId;
       String user = "{\"username\":\"" + username + "\"}";
       this.messagingTemplate.convertAndSend("/topic/join", user);
      /*  MessageHeaders headers = event.getMessage().getHeaders();
       // Map<String, Object> sessionAttributes = SimpMessageHeaderAccessor.getFirstNativeHeader(headers);

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        accessor.addNativeHeader("user-name", "pena");
        accessor.setNativeHeader("accept-version", "2");
        for (int i = 0; i < 100; i++) {
            System.out.println("perkele");
        //    System.out.println(sessionAttributes.get("headers").toString());
        }
        System.out.println(accessor.getNativeHeader("user-name"));
        Principal user = SimpMessageHeaderAccessor.getUser(headers);
        if (user == null) {
            return;
        }
        String id = SimpMessageHeaderAccessor.getSessionId(headers);*/
    }
}
