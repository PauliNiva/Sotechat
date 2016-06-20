package sotechat.connectionEvents;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Luokka, johon on talletettu tieto sessioiden statuksesta. Statukseen
 * pääsee käsiksi session sessionId:n perusteella. Luokka tarjoaa myös
 * metodit session statuksen muuttamiseksi.
 */
public class ConnectionRepo {

    /**
     * HashMap, johon talletetaan tieto session statuksesta. Tietyllä
     * sessionId:llä päästään käsiksi booleaniin, jonka arvo on true, jos
     * session status on aktiivinen ja false, jos sessio ei ole aktiivinen.
     */
    private Map<String, Boolean> connectedSessions;

    /**
     * ConnectionRepo-luokan konstruktori.
     */
    public ConnectionRepo() {
        this.connectedSessions = new HashMap<>();
    }

    /**
     * Metodi, jonka avulla voidaan muuttaa tietyn session status aktiiviseksi.
     *
     * @param id SessionId
     */
    public final void setSessionStatusToConnected(final String id) {
        this.connectedSessions.put(id, true);
    }

    /**
     * Metodi, jonka avulla voidaan muuttaa tietyn session status
     * inaktiiviseksi.
     *
     * @param id SessionId
     */
    public final void setSessionStatusToDisconnected(final String id) {
        this.connectedSessions.put(id, false);
    }

    /**
     * Metodi, jonka avulla voidaan tarkistaa session status id:n perusteella.
     *
     * @param id SessionId
     * @return Palauttaa totuusarvon true, jos session status on connected
     * ja false, jos se on disconnected.
     */
    public final boolean sessionIsConnected(final String id) {
        return this.connectedSessions.get(id);
    }
}
