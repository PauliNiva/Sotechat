package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.queue.Queue;
import sotechat.queue.QueueItem;

import java.util.List;

/**
 * QueueService luokka tarjoaa palvelut jonoon lisäämiseen, jonosta
 * poistamiseen ja jonon tarkasteluun
 * Created by varkoi on 2.6.2016.
 */
@Service
public class QueueService {

    /**
     * Jono olio, johon tallennetaan jonottajien tiedot
     */

    Queue queue;

    /**
     * konstruktori alustaa jonon parametrina annetulla jono oliolla
     * @param queue jono olio, johon jonottavien käyttäjien tiedot tallennetaan
     */
    @Autowired
    public QueueService(Queue queue){
        this.queue = queue;
    }

    /**
     * addToQueue -metodi lisää jonon perälle alkion, jossa tiedot jonottajan
     * kanavaid:stä keskustelun aihealueesta ja jonottajan käyttäjänimestä
     * @param channelId jonottajan kanavaid
     * @param category keskustelun aihealue
     * @param username jonottajan käyttäjänimi
     */
    public final void addToQueue(String channelId, String category,
                                 String username){
        queue.addTo(channelId, category, username);
    }

    /**
     * queueToString -metodi palauttaa JSON -muotoisen taulukko esityksen
     * jonon alkioista
     * @return JSON -muotoinen taulukko jonon alkioista
     */
    public final String queueToString() {
        List<QueueItem> list = queue.returnQueue();
        String json = "{\"jono\": [";
        for(QueueItem item : list){
            if(list.indexOf(item)!=0) json += ", ";
            json += jsonObject(item);
        }
        json += "]}";
        return json;
    }

    /**
     * firstOfQueue -metodi palauttaa jonon ensimmäisen alkion JSON -olion
     * muodossa ja samalla poistaa sen jonosta tai jos jono on tyhjä
     * palautetaan tyhjä String
     * @return jonon ensimmäinen alkio JSON oliona, jossa muuttujina kanavaid,
     * keskustelun aihealue (kategoria) sekä käyttäjänimi
     */
    public final String firstOfQueue() {
        try {
            QueueItem first = queue.getFirst();
            return jsonObject(first);
        } catch (Exception e){
            return "";
        }
    }

    /**
     * firstOfCategory -metodi palauttaa ensimmäisen alkion jonosta parametrina
     * annetusta kategoriasta JSON olion muodossa ja samalla poistaa sen
     * jonosta tai jos jono on tyhjä palautetaan tyhjä String
     * @param category aihealue, jonka keskusteluja haetaan
     * @return jonon ensimmäinen alkio haetusta kategoriasta JSON oliona, jossa
     * muuttujina kanavaid, keskustelun aihealue (kategoria) sekä käyttäjänimi
     */
    public final String firstOfCategory(String category){
        try {
            QueueItem first = queue.getFirstFrom(category);
            return jsonObject(first);
        } catch (Exception e){
            return "";
        }
    }

    /**
     * Poistaa jonosta alkion kanavaid:n perusteella ja palauttaa sitä
     * esittävän JSON -olion
     * @param channelId kanavaid, jota vastaava alkio halutaan ottaa jonosta
     * @return haettua kanavaid:tä vastaava alkio JSON -oliona, jossa
     * muuttujina kanavaid, keskustelun aihealue (kategoria) sekä käyttäjänimi
     */
    public final String removeFromQueue(String channelId){
        try {
            QueueItem removed = queue.remove(channelId);
            return jsonObject(removed);
        } catch (Exception e){
            return "";
        }
    }

    /**
     * queueLength -metodi palauttaa jonon pituuden
     * @return jonon alkioiden määrä
     */
    public final int queueLength(){
        return queue.length();
    }

    /**
     * queueLength -metodi palauttaa parametrina annettua vanavaid:tä vastaavaa
     * alkiota edeltävän jonon pituuden
     * @param channelId kanavaid, jota vastaavaa alkiota edeltävän jonon pituus
     *                  halutaan selvittää
     * @return  alkioiden määrä, jotka edeltävät haettua alkiota
     */
    public final int queueLength(final String channelId){
        return queue.itemsBefore(channelId);
    }

    /**
     * jsonObject -metodi luo JSON -olio muotoisen String esityksen parametrina
     * annetusta QueueItemista
     * @param item jonon alkio
     * @return JSON -olio muotoinen esitys jonon alkiosta
     */
    private final String jsonObject(QueueItem item){
        String json = "{";
        json += "\"channelId\": \"" + item.getChannelId() + "\", ";
        json += "\"category\": \"" + item.getCategory() + "\", ";
        json += "\"username\": \"" + item.getUsername() + "\"}";
        return json;
    }

}
