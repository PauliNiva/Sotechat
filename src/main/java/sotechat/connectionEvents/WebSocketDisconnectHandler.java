package sotechat.connectionEvents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.service.QueueTimeoutService;

import java.security.Principal;

/**
 * Luokka, jossa määritellään mitä tapahtuu, kun WebSocket-yhteys katkeaa.
 *
 * @param <S> Abstrakti olio.
 */
@Component
public class WebSocketDisconnectHandler<S>
        implements ApplicationListener<SessionDisconnectEvent> {

    /**
     * SessionRepo-olio, jonka perusteella voidaan selvittää, onko
     * tietty sessio aktiivinen vai inaktiivinen.
     */
    @Autowired
    private SessionRepo sessionRepo;

    /**
     * Olio, jonka vastuuseen kuuluu poistaa inaktiiviset käyttäjät jonosta.
     */
    @Autowired
    private QueueTimeoutService queueTimeoutService;

    /**
     * Konstruktori.
     */
    public WebSocketDisconnectHandler() {
    }

    /**
     * Mitä tapahtuu, kun WebSocket-yhteys katkeaa. Eli käynnistetään odotus,
     * jonka jälkeen inaktiiviset sessiot poistetaan jonosta. Lisäksi
     * asetetaan WebSocket-yhteyden katkeamiseen liittyneen sessionin tilaksi
     * disconnected ConnectionRepo-olioon.
     *
     * @param event Yhteyden katkeamistapahtuma.
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
        if (userSession != null) { //TODO:Muuta?
            userSession.set("connectionStatus", "disconnected");
        }

    }
}
