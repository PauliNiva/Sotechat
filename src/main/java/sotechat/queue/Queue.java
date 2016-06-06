package sotechat.queue;

import java.util.List;

/**
 * Jono rajapinta maarittelee jonon metodit
 * Created by varkoi on 2.6.2016.
 */
public interface Queue {
    /**
     * Lisaa uuden alkion jonon peraan, jolla parametrina annetut tiedot
     * muuttujinaan
     * @param channelId jonottajan kanavan id
     * @param category keskustelun aihealue
     * @param username jonottajan kayttajanimi
     */
    boolean addTo(String channelId,
               String category,
               String username);


    /**
     * metodi palauttaa jonon ensimmaisen alkion
     * @return jonon ensimmainen alkio
     */
    QueueItem pollFirst();

    /**
     * metodi palauttaa kategorian ensimmaisen alkion jonossa
     * @param category keskustelun aihealue
     * @return kategorian ensimmainen alkio jonossa
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
     * metodi palauttaa haettua alkiota edeltavan jonon pituuden
     * @param channelId haetun alkion kanavaid
     * @return edeltavan jonon pituus
     */
    int itemsBefore(String channelId);

    /**
     * metodi palauttaa haettua alkiota edeltävän jonon pituuden
     * parametrina annetussa kategoriassa
     * @param channelId haetun alkion kanavaid
     * @param category tarkasteltava kategoria
     * @return edeltävän jonon pituus annetussa kategoriassa
     */
    int itemsBeforeIn(String channelId, String category);

    /**
     * metodi palauttaa viitteen jonoon
     * @return jono
     */
    List<QueueItem> returnQueue();
}
