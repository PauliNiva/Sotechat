package sotechat.connectionEvents;

/**
 * Luokka ei viela kaytossa.
 */
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public class WebSocketDisconnectHandler<S>
        implements ApplicationListener<SessionDisconnectEvent> {

    private ConnectionRepo connectionRepo;

    private ConnectionHandler connectionHandler;

    public WebSocketDisconnectHandler(
            final ConnectionRepo pConnectionRepo,
            final ConnectionHandler pConnectionHandler) {
        this.connectionRepo = pConnectionRepo;
        this.connectionHandler = pConnectionHandler;
    }

   public void onApplicationEvent(final SessionDisconnectEvent event) {
       MessageHeaders headers = event.getMessage().getHeaders();
       String sessionId = SimpMessageHeaderAccessor
               .getSessionAttributes(headers)
               .get("SPRING.SESSION.ID").toString();
       this.connectionRepo.setSessionStatusToDisconnected(sessionId);
       this.connectionHandler.setSessionId(sessionId);
    }
}
