package sotechat.connectionEvents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;

/**
 * Luokka, jossa määritellään mitä tapahtuu, kun WebSocket-yhteys muodostuu.
 *
 * @param <S> Abstrakti olio.
 */
public class WebSocketConnectHandler<S>
        implements ApplicationListener<SessionConnectEvent> {

    /**
     * ConnectionRepo-olio, jonka perusteella voidaan selvittää, onko
     * tietty sessio aktiivinen vai inaktiivinen.
     */
    @Autowired
    private ConnectionRepo connectionRepo;

    /**
     * Konstruktori.
     */
    public WebSocketConnectHandler() {
    }

    /**
     * Metodi, jossa määritellään mitä tapahtuu, kun WebSocket-yhteys
     * muodostuu. Siis talletetaan ConnectionRepoon tieto, että tietyn
     * session status on aktiivinen.
     *
     * @param event Yhteyden muodostumistapahtuma.
     */
   public final void onApplicationEvent(final SessionConnectEvent event) {
       MessageHeaders headers = event.getMessage().getHeaders();
       String sessionId = SimpMessageHeaderAccessor
               .getSessionAttributes(headers)
               .get("SPRING.SESSION.ID").toString();
       this.connectionRepo.setSessionStatusToConnected(sessionId);
    }
}
