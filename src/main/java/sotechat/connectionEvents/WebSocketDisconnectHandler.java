package sotechat.connectionEvents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import sotechat.controller.MessageBroker;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.service.QueueTimeoutService;

import java.security.Principal;

/**
 * Maarittelee mitä tapahtuu, kun <code>WebSocket</code>-yhteys katkeaa.
 *
 * @param <S> Abstrakti olio.
 */
@Component
public class WebSocketDisconnectHandler<S>
        implements ApplicationListener<SessionDisconnectEvent> {

    /**
     * <code>SessionRepo</code>-olio, jonka perusteella voidaan selvittaa, onko
     * tietty sessio aktiivinen vai inaktiivinen.
     */
    @Autowired
    private SessionRepo sessionRepo;

    /**
     * Olio, jonka vastuuseen kuuluu poistaa inaktiiviset kayttajat jonosta.
     */
    @Autowired
    private QueueTimeoutService queueTimeoutService;

    /**
     * Viestienvalittaja.
     */
    @Autowired
    private MessageBroker broker;

    /**
     * Konstruktori.
     */
    public WebSocketDisconnectHandler() {
    }

    /**
     * Kaynnistaa <code>WebSocket</code>-yhteyden katketessa odotuksen.
     * Odotuksen jalkeen inaktiiviset sessiot poistetaan jonosta.
     *
     * @param event Yhteydenkatkeamistapahtuma.
     */
    public final void onApplicationEvent(final SessionDisconnectEvent event) {
       MessageHeaders headers = event.getMessage().getHeaders();
       String sessionId = SimpMessageHeaderAccessor
               .getSessionAttributes(headers)
               .get("SPRING.SESSION.ID").toString();
       Principal user = SimpMessageHeaderAccessor.getUser(headers);
       if (user == null) {
           this.queueTimeoutService
                   .initiateWaitBeforeScanningForInactiveUsers(
                           sessionId);
       }
        Session userSession = this.sessionRepo
                .getSessionFromSessionId(sessionId);
        if (userSession != null) {
            userSession.set("connectionStatus", "disconnected");
            broker.sendLeaveNotices(userSession);
        }

    }
}
