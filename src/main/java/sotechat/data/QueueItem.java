package sotechat.data;

/**
 * QueueItem olioon tallennetaan jonottajan tiedot.
 */
public class QueueItem {

    /** Jonottavan kanavan id.  */
    private String channelId;

    /** Keskustelun aihealue. */
    private String category;

    /** Jonottavan kayttajan nimi. */
    private String username;

    /** Konstruktori alustaa olion annetuilla parametreilla.
     * @param pChannelId jonottavan henkilon kanavan id
     * @param pCategory keskustelun aihealue
     * @param pUsername jonottavan kayttajan nimi
     */
    public QueueItem(
            final String pChannelId,
            final String pCategory,
            final String pUsername
    ) {
        this.channelId = pChannelId;
        this.category = pCategory;
        this.username = pUsername;
    }

    /** Palauttaa jonottavan henkilon kanavan id:n.
     * @return  kanavan id
     */
    public final String getChannelId() {
        return channelId;
    }

    /** Palauttaa jonottavan henkilon keskustelun aihealueen.
     * @return keskustelun aihealue
     */
    public final String getCategory() {
        return category;
    }

    /** Palauttaa jonottavan henkilon nimen.
     * @return henkilon nimi
     */
    public final String getUsername() {
        return username;
    }

}
