package sotechat.data;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

/** Jono odottaville asiakkaille chatissa.
 */
@Component
public class QueueImpl implements Queue {

    /** QueueItemeista koostuva jono.
     */
    private LinkedList<QueueItem> queue;

    /** Konstruktori alustaa jonon uudeksi listaksi QueueItemeja.
     */
    public QueueImpl() {
        this.queue = new LinkedList<QueueItem>();
    }

    /** Konstruktori alustaa jonon uudeksi listaksi,
     * johon lisataan parametrina annetut QueueItemit.
     * @param items jonoon lisattavat QueueItemit
     */
    public QueueImpl(final QueueItem... items) {
        this.queue = new LinkedList<QueueItem>();
        for (QueueItem item: items) {
            this.queue.addLast(item);
        }
    }

    /** addTo -metodi lisaa jonoon uuden QueueItemin, johon on talletettu
     * jonottavan kayttajan channelId, category seka jonottavan
     * kayttajan nimi.
     * @param channelId jonottajan kanavan id
     * @param category keskustelun aihealue
     * @param username jonottajan kayttajanimi
     * @return aina true
     */
    @Override
    public final synchronized boolean addTo(
            final String channelId,
            final String category,
            final String username
    ) {
        QueueItem item = new QueueItem(channelId, category, username);
        return queue.offerLast(item);
    }

    /** pollFirst -metodi poistaa ja palauttaa jonon ensimmaisen QueueItemin.
     * Jos jono on tyhja palautetaan null.
     * @return jonon ensimmainen QueueItem tai null, jos jono on tyhja.
     */
    @Override
    public final synchronized QueueItem pollFirst() {
        return this.queue.pollFirst();
    }

    /** pollFirstFrom -metodi palauttaa jonossa ensimmaisena olevan QueueItemin
     * parametrina annetusta kategoriasta ja poistaa itemin jonosta.
     * @param category minka aihealueen ensimmainen QueueItem halutaan
     * @return aihealueen ensimmainen QueueItem
     */
    @Override
    public final synchronized QueueItem pollFirstFrom(final String category) {
        ListIterator<QueueItem> iterator = queue.listIterator(0);
        while (iterator.hasNext()) {
            QueueItem next = iterator.next();
            if (next.getCategory().equals(category)) {
                iterator.remove();
                return next;
            }
        }
        return null;
    }

    /** Poistaa ja palauttaa jonosta QueueItemin parametrina annetun kanavaid:n
     * perusteella.
     * @param channelId etsittavan QueueItemin kanavaid
     * @return  QueueItem jolla on haettu kanavaid
     */
    @Override
    public final synchronized QueueItem remove(final String channelId) {
        ListIterator<QueueItem> iterator = queue.listIterator(0);
        while (iterator.hasNext()) {
            QueueItem next = iterator.next();
            if (next.getChannelId().equals(channelId)) {
                iterator.remove();
                return next;
            }
        }
        return null;
    }

    /** length -metodi palauttaa jonon pituuden.
     * @return jonon pituus
     */
    @Override
    public final synchronized int length() {
        return queue.size();
    }

    /** Palauttaa parametrina annetun kanavaid:n omaavaa
     * QueueItemia edeltavan jonon pituuden.
     * @param channelId Haetun QueueItemin kanavaid
     * @return kuinka monta QueueItemia on jonossa ennen haettua QueueItemia
     */
    @Override
    public final synchronized int itemsBefore(final String channelId) {
        ListIterator<QueueItem> iterator = queue.listIterator(0);
        int count = 0;
        while (iterator.hasNext()) {
            QueueItem next = iterator.next();
            if (next.getChannelId().equals(channelId)) {
                return count;
            }
            count++;
        }
        return count;
    }

    /** itemsBeforeIn -metodi palauttaa parametrina annetun kanavaid:n omaavaa
     * QueueItemia edeltävän jonon pituuden parametrina annetuissa aihealue
     * kategoriassa.
     * @param channelId Haetun QueueItemin kanavaid
     * @param category keskustelun aihealue jossa olevia QueueItemejä
     *                 tarkastellaan
     * @return kuinka monta QueueItemia on jonossa ennen haettua QueueItemia
     * annetussa kategoriassa
     */
    public final synchronized int itemsBeforeIn(
            final String channelId,
            final String category
    ) {
        ListIterator<QueueItem> iterator = queue.listIterator(0);
        int count = 0;
        while (iterator.hasNext()) {
            QueueItem next = iterator.next();
            if (next.getChannelId().equals(channelId)) {
                return count;
            }
            if (next.getCategory().equals(category)) {
                count++;
            }
        }
        return count;
    }

    /* Palauttaa jono-olion.
     * @return jono
     */
    @Override
    public final synchronized List<QueueItem> getQueue() {
        return this.queue;
    }
}
