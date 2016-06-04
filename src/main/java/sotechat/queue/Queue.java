package sotechat.queue;

import java.util.List;

/**
 * Jono rajapinta määrittelee jonon metodit
 * Created by varkoi on 2.6.2016.
 */
public interface Queue {
    /**
     * Lisää uuden alkion jonon perään, jolla parametrina annetut tiedot
     * muuttujinaan
     * @param channelId jonottajan kanavan id
     * @param category keskustelun aihealue
     * @param username jonottajan käyttäjänimi
     */
    boolean addTo(String channelId,
               String category,
               String username);


    /**
     * metodi palauttaa jonon ensimmäisen alkion
     * @return jonon ensimmäinen alkio
     */
    QueueItem pollFirst();

    /**
     * metodi palauttaa kategorian ensimmäisen alkion jonossa
     * @param category keskustelun aihealue
     * @return kategorian ensimmäinen alkio jonossa
     */
    QueueItem getFirstFrom(String category);

    /**
     * metodi poistaa haetun alkion jonosta
     * @param channelId haettavan alkion kanavaid
     * @return poistettu alkio
     */
    QueueItem remove(String channelId);

    /**
     * metodi palauttaa jonon pituuden
     * @return jonon pituus
     */
    int length();

    /**
     * metodi palauttaa haettua alkiota edeltävän jonon pituuden
     * @param channelId haetun alkion kanavaid
     * @return edeltävän jonon pituus
     */
    int itemsBefore(String channelId);

    /**
     * metodi palauttaa viitteen jonoon
     * @return jono
     */
    List<QueueItem> returnQueue();
}
