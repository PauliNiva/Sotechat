package sotechat.connectionEvents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

/**
 * Luokka, jossa määritellään mitä tapahtuu, kun WebSocket-yhteys muodostuu.
 *
 * @param <S> Abstrakti olio.
 */
@Component
public class WebSocketConnectHandler<S>
        implements ApplicationListener<SessionConnectEvent> {

    /**
     * SessionRepo-olio, jonka perusteella voidaan selvittää, onko
     * tietty sessio aktiivinen vai inaktiivinen.
     */
    @Autowired
    private SessionRepo sessionRepo;

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
       Session userSession = this.sessionRepo
               .getSessionFromSessionId(sessionId);
       userSession.set("connectionStatus", "connected");
    }
}
