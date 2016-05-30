/*package sotechat.websocket;

/**
 * Luokka ei vielä käytössä.
 */
/*import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.messaging.SessionConnectEvent;

public class WebSocketConnectHandler<S>
        implements ApplicationListener<SessionConnectEvent> {

    private SimpMessageSendingOperations messagingTemplate;

    public WebSocketConnectHandler(
            final SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

   public void onApplicationEvent(SessionConnectEvent event) {
      System.out.println(session);
        Principal user = SimpMessageHeaderAccessor.getUser(headers);
        if (user == null) {
            return;
        }
        String id = SimpMessageHeaderAccessor.getSessionId(headers);
    }
}*/
