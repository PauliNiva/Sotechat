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
    //   System.out.println(session);
      /*  Principal user = SimpMessageHeaderAccessor.getUser(headers);
        if (user == null) {
            return;
        }
        String id = SimpMessageHeaderAccessor.getSessionId(headers);*/
    }
}
