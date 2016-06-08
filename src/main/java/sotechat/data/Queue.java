package sotechat.data;

import java.util.List;

/** Jono odottaville asiakkaille chatissa.
 */
public interface Queue {

    /** Lisaa uuden alkion jonon peraan, jolla
     * parametrina annetut tiedot muuttujinaan.
     * @param channelId jonottajan kanavan id
     * @param category keskustelun aihealue
     * @param username jonottajan kayttajanimi
     * @return aina true.
     */
    boolean addTo(String channelId,
               String category,
               String username);


    /** Poistaa jonon ensimmaisen alkion ja palauttaa sen.
     * @return poistettu alkio
     */
    QueueItem pollFirst();

    /** Metodi poistaa kategorian ensimmaisen alkion ja palauttaa sen.
     * @param category keskustelun aihealue
     * @return poistettu alkio
     */
    QueueItem pollFirstFrom(String category);

    /** Metodi poistaa kanavaId:n perusteella haetun alkion jonosta.
     * @param channelId haettavan alkion kanavaid
     * @return poistettu alkio
     */
    QueueItem remove(String channelId);

    /** Palauttaa jonon pituuden.
     * @return jonon pituus
     */
    int length();

    /** Palauttaa haettua alkiota edeltavan jonon pituuden.
     * @param channelId haetun alkion kanavaid
     * @return edeltavan jonon pituus
     */
    int itemsBefore(String channelId);

    /** Palauttaa haettua alkiota edeltavan jonon pituuden
     * parametrina annetussa kategoriassa.
     * @param channelId haetun alkion kanavaid
     * @param category tarkasteltava kategoria
     * @return edeltävän jonon pituus annetussa kategoriassa
     */
    int itemsBeforeIn(String channelId, String category);

    /** Palauttaa viitteen jonoon.
     * @return jono
     */
    List<QueueItem> getQueue();
}
