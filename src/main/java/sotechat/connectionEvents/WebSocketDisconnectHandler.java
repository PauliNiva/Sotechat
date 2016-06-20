package sotechat.connectionEvents;

/**
 * Luokka ei viela kaytossa.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

public class WebSocketDisconnectHandler<S>
        implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    private ConnectionRepo connectionRepo;

    @Autowired
    private ConnectionHandler connectionHandler;

    public WebSocketDisconnectHandler() {
    }

   public void onApplicationEvent(final SessionDisconnectEvent event) {
       MessageHeaders headers = event.getMessage().getHeaders();
       String sessionId = SimpMessageHeaderAccessor
               .getSessionAttributes(headers)
               .get("SPRING.SESSION.ID").toString();
       Principal user = SimpMessageHeaderAccessor.getUser(headers);
       if (user != null) {
           this.connectionHandler
                   .initiateWaitBeforeScanningForInactiveProfessional(sessionId);
       } else {
           this.connectionHandler
                   .initiateWaitBeforeScanningForInactiveUsers(sessionId);
       }
       System.out.println(SimpMessageHeaderAccessor.getUser(headers));
       this.connectionRepo.setSessionStatusToDisconnected(sessionId);
    }
}
