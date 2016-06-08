package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.data.Queue;
import sotechat.data.QueueItem;

import java.util.List;

/** Tarjoaa palvelut jonoon lisaamiseen, jonosta
 * poistamiseen ja jonon tarkasteluun.
 */
@Service
public class QueueService {

    /** Jono-olio, johon tallennetaan jonottajien tiedot. */
    Queue queue;

    /** Konstruktori alustaa jonon parametrina annetulla jono oliolla.
     * @param queue jono-olio, johon jonottavien kayttajien tiedot tallennetaan
     */
    @Autowired
    public QueueService(final Queue queue) {
        this.queue = queue;
    }

    /**
     * addToQueue -metodi lisaa jonon peralle alkion, jossa tiedot jonottajan
     * kanavaid:sta keskustelun aihealueesta ja jonottajan kayttajanimesta.
     * Palauttaa true jos lisays onnistui.
     * @param channelId jonottajan kanavaid
     * @param category keskustelun aihealue
     * @param username jonottajan kayttajanimi
     * @return true jos lisays onnitui
     */
    public final boolean addToQueue(
            final String channelId,
            final String category,
            final String username) {
        return queue.addTo(channelId, category, username);
    }

    /** Palauttaa jonon Stringina, joka nayttaa JSON-ystavalliselta taulukolta.
     * TODO: esimerkkioutput.
     * @return string
     */
    @Override
    public final String toString() {
        List<QueueItem> list = queue.getQueue();
        String json = "{\"jono\": [";
        for (QueueItem item : list) {
            if (list.indexOf(item) != 0) {
                json += ", ";
            }
            json += jsonObject(item);
        }
        json += "]}";
        return json;
    }

    /**
     * firstOfQueue -metodi palauttaa jonon ensimmaisen alkion JSON -olion
     * muodossa ja samalla poistaa sen jonosta tai jos jono on tyhja
     * palautetaan tyhja String.
     * @return jonon ensimmainen alkio JSON oliona, jossa muuttujina kanavaid,
     * keskustelun aihealue (kategoria) seka kayttajanimi
     */
    public final String firstOfQueue() {
        try {
            QueueItem first = queue.pollFirst();
            return jsonObject(first);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * firstOfCategory -metodi palauttaa ensimmaisen alkion jonosta parametrina
     * annetusta kategoriasta JSON olion muodossa ja samalla poistaa sen
     * jonosta tai jos jono on tyhja palautetaan tyhja String.
     * @param category aihealue, jonka keskusteluja haetaan
     * @return jonon ensimmainen alkio haetusta kategoriasta JSON oliona, jossa
     * muuttujina kanavaid, keskustelun aihealue (kategoria) seka kayttajanimi
     */
    public final String firstOfCategory(final String category) {
        try {
            QueueItem first = queue.pollFirstFrom(category);
            return jsonObject(first);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Poistaa jonosta alkion kanavaid:n perusteella ja palauttaa sita
     * esittavan JSON -olion.
     * @param channelId kanavaid, jota vastaava alkio halutaan ottaa jonosta
     * @return haettua kanavaid:ta vastaava alkio JSON -oliona, jossa
     * muuttujina kanavaid, keskustelun aihealue (kategoria) seka kayttajanimi
     */
    public final String removeFromQueue(final String channelId) {
        try {
            QueueItem removed = queue.remove(channelId);
            return jsonObject(removed);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * queueLength -metodi palauttaa jonon pituuden.
     * @return jonon alkioiden maara
     */
    public final int queueLength() {
        return queue.length();
    }

    /**
     * queueLength -metodi palauttaa parametrina annettua kanavaid:tä vastaavaa
     * alkiota edeltävän jonon pituuden.
     * @param channelId kanavaid, jota vastaavaa alkiota edeltävän jonon pituus
     *                  halutaan selvittää
     * @return  alkioiden määrä, jotka edeltävät haettua alkiota
     */
    public final int queueLength(final String channelId) {
        return queue.itemsBefore(channelId);
    }

    /**
     * queueLength -metodi palauttaa parametrina annettua kanavaid:tä vastaavaa
     * alkiota edeltävän jonon pituuden parametrina annetussa kategoriassa.
     * @param channelId kanavaid, jota vastaavaa alkiota edeltävän jonon pituus
     *                  halutaan selvittää
     * @param category aihealue, jonka alkiot otetaan laskussa mukaan
     * @return aihealueeseen kuuluvien alkioiden määrä, jotka edeltävät haettua
     * alkiota
     */
    public final int queueLength(
            final String channelId,
            final String category
    ) {
        return queue.itemsBeforeIn(channelId, category);
    }

    /**
     * jsonObject -metodi luo JSON -olio muotoisen String esityksen parametrina
     * annetusta QueueItemista.
     * @param item jonon alkio
     * @return JSON -olio muotoinen esitys jonon alkiosta
     */
    private String jsonObject(QueueItem item) {
        String json = "{";
        json += "\"channelId\": \"" + item.getChannelId() + "\", ";
        json += "\"category\": \"" + item.getCategory() + "\", ";
        json += "\"username\": \"" + item.getUsername() + "\"}";
        return json;
    }

}
