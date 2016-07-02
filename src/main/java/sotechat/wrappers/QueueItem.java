package sotechat.wrappers;

/**
 * <code>Wrapper</code>-luokka jonottajalle.
 */
public class QueueItem {

    /**
     * Jonottavan kanavan tunnus.
     */
    private String channelId;

    /**
     * Keskustelun aihealue.
     */
    private String category;

    /**
     * Jonottavan kayttajan kayttajanimi.
     */
    private String username;

    /**
     * Konstruktori alustaa olion annetuilla parametreilla.
     *
     * @param pChannelId Jonottavan henkilon kanavatunnus.
     * @param pCategory Keskustelun aihealue.
     * @param pUsername Jonottavan kayttajan kayttajanimi.
     */
    public QueueItem(final String pChannelId, final String pCategory,
                     final String pUsername) {
        this.channelId = pChannelId;
        this.category = pCategory;
        this.username = pUsername;
    }

    /**
     * Palauttaa jonottavan henkilon kanavan tunnuksen.
     *
     * @return  Kanavan tunnus.
     */
    public final String getChannelId() {
        return channelId;
    }

    /**
     * Palauttaa jonottavan henkilon keskustelun aihealueen.
     *
     * @return Keskustelun aihealue.
     */
    public final String getCategory() {
        return category;
    }

    /**
     * Palauttaa jonottavan henkilon kayttajanimen.
     *
     * @return Henkilon kayttajanimi.
     */
    public final String getUsername() {
        return username;
    }

    /**
     * Palauttaa <code>String</code>-olion, joka on muotoiltu
     * <code>JSON</code>-muotoon.
     *
     * @return <code>JSON</code> merkkijonona.
     */
    @Override
    public final String toString() {
        String json = "{";
        json += "\"channelId\": \"" + getChannelId() + "\", ";
        json += "\"category\": \"" + getCategory() + "\", ";
        json += "\"username\": \"" + getUsername() + "\"}";
        return json;
    }

}
