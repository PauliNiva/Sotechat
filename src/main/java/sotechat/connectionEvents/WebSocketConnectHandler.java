package sotechat.connectionEvents;

/**
 * Luokka ei viela kaytossa.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;


public class WebSocketConnectHandler<S>
        implements ApplicationListener<SessionConnectEvent> {

    @Autowired
    private ConnectionRepo connectionRepo;

    public WebSocketConnectHandler() {
    }

   public void onApplicationEvent(final SessionConnectEvent event) {
       MessageHeaders headers = event.getMessage().getHeaders();
       String sessionId = SimpMessageHeaderAccessor
               .getSessionAttributes(headers)
               .get("SPRING.SESSION.ID").toString();
       this.connectionRepo.setSessionStatusToConnected(sessionId);
    }
}
