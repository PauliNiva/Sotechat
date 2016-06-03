package sotechat.queue;

/**
 * QueueItem olioon tallennetaan jonottajan tiedot
 * Created by Asus on 1.6.2016.
 */
public class QueueItem {
    /**
     * jonottavan kanavan id
     */
    String channelId;
    /**
     * keskustelun aihealue
     */
    String category;
    /**
     * jonottavan käyttäjän nimi
     */
    String username;

    /**
     * konstruktori alustaa olion annetuilla parametreilla
     * @param channelId jonottavan henkilön kanavan id
     * @param category keskustelun aihealue
     * @param username jonottavan käyttäjän nimi
     */
    public QueueItem(String channelId, String category, String username) {
        this.channelId = channelId;
        this.category = category;
        this.username = username;
    }

    /**
     * palauttaa jonottavan henkilön kanavan id:n
     * @return  kanavan id
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * palauttaa jonottavan henkilön keskustelun aihealueen
     * @return keskustelun aihealue
     */
    public String getCategory() {
        return category;
    }

    /**
     * palauttaa jonottavan henkilön nimen
     * @return henkilön nimi
     */
    public String getUsername() {
        return username;
    }

}
