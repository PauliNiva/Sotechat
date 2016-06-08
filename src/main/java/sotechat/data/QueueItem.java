package sotechat.data;

/** QueueItem olioon tallennetaan jonottajan tiedot
 */
public class QueueItem {

    /** Jonottavan kanavan id.  */
    String channelId;

    /** Keskustelun aihealue. */
    String category;

    /** Jonottavan kayttajan nimi. */
    String username;

    /** Konstruktori alustaa olion annetuilla parametreilla.
     * @param channelId jonottavan henkilon kanavan id
     * @param category keskustelun aihealue
     * @param username jonottavan kayttajan nimi
     */
    public QueueItem(
            final String channelId,
            final String category,
            final String username
    ) {
        this.channelId = channelId;
        this.category = category;
        this.username = username;
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
