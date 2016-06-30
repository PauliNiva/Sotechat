package sotechat.wrappers;

/**
 * <code>Wrapper</code>-luokka keskustelun tiedoille.
 */
public class ConvInfo {

    /**
     * Kanavatunnus.
     */
    private String channelId;

    /**
     * Aikaleima.
     */
    private String date;

    /**
     * Keskusteluun liittyva ammattilainen.
     */
    private String person;

    /**
     * Aihealue.
     */
    private String category;

    /**
     * Konstruktori.
     *
     * @param pChannelId p
     * @param pDate p
     * @param pPerson p
     * @param pCategory p
     */
    public ConvInfo(final String pChannelId, final String pDate,
                    final String pPerson, final String pCategory) {
        this.channelId = pChannelId;
        this.date = pDate;
        this.person = pPerson;
        this.category = pCategory;
    }

    /**
     * Hakee kanavatunnuksen.
     *
     * @return Kanavatunnus.
     */
    public final String getChannelId() {
        return channelId;
    }

    /**
     * Hakee keskustelun aikaleiman.
     *
     * @return Aikaleima.
     */
    public final String getDate() {
        return date;
    }

    /**
     * Hakee keskusteluun liittyvan ammattilaisen.
     *
     * @return Keskusteluun liittyva ammattilainen.
     */
    public final String getPerson() {
        return person;
    }

    /**
     * Hakee keskustelun aihealueen.
     *
     * @return Keskustelun aihealue.
     */
    public String getCategory() {
        return category;
    }

}
