package sotechat.connectionEvents;

/**
 * Luokka ei viela kaytossa.
 */
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;


public class WebSocketConnectHandler<S>
        implements ApplicationListener<SessionConnectEvent> {

    private ConnectionRepo connectionRepo;

    public WebSocketConnectHandler(
            final ConnectionRepo pConnectionRepo) {
        this.connectionRepo = pConnectionRepo;
    }

   public void onApplicationEvent(final SessionConnectEvent event) {
       MessageHeaders headers = event.getMessage().getHeaders();
       String sessionId = SimpMessageHeaderAccessor
               .getSessionAttributes(headers)
               .get("SPRING.SESSION.ID").toString();
       this.connectionRepo.setSessionStatusToConnected(sessionId);
    }
}
