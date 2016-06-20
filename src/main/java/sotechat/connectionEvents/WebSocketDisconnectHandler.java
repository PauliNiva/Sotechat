package sotechat.connectionEvents;

/**
 * Luokka, jossa määritellään mitä tapahtuu, kun WebSocket-yhteys katkeaa.
 *
 * @param <S> Abstrakti olio.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

public class WebSocketDisconnectHandler<S>
        implements ApplicationListener<SessionDisconnectEvent> {

    /**
     * ConnectionRepo-olio, jonka perusteella voidaan selvittää, onko
     * tietty sessio aktiivinen vai inaktiivinen.
     */
    @Autowired
    private ConnectionRepo connectionRepo;

    /**
     * Olio, jonka vastuuseen kuuluu poistaa inaktiiviset käyttäjät jonosta.
     */
    @Autowired
    private ConnectionHandler connectionHandler;

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
   /*    if (user != null) {
           this.connectionHandler
                   .initiateWaitBeforeScanningForInactiveProfessional(
                   sessionId);
       }*/
       if (user == null) {
           this.connectionHandler
                   .initiateWaitBeforeScanningForInactiveUsers(
                           sessionId);
       }
       this.connectionRepo.setSessionStatusToDisconnected(sessionId);
    }
}
