package sotechat.wrappers;

/**
 * Asiakasohjelman palvelimelle lahettama viesti paketoidaan
 * <code>MsgToServer</code>-olion sisalle, ennen kuin palvelimen
 * <code>ChatController</code>-luokka kasittelee sen.
 */
public class MsgToServer {

    /**
     * Kayttajan Id.
     */
    private String userId;

    /**
     * Kanavatunnus.
     */
    private String channelId;

    /**
     * Viestin sisalto.
     */
    private String content;

    /**
     * Konstruktorin lisaaminen rikkoo <code>Spring</code>:in, tarkemmin
     * <code>ChatController</code>:in.
     * Siksi staattinen metodi toimittaa konstruktorin virkaa
     * .
     * @param pUserId p
     * @param pChannelId p
     * @param pContent p
     * @return uusi <code>MsgToServer</code>-olio annetuilla arvoilla.
     */
    public static MsgToServer create(final String pUserId,
                                     final String pChannelId,
                                     final String pContent) {
        MsgToServer instance = new MsgToServer();
        instance.setUserId(pUserId);
        instance.setChannelId(pChannelId);
        instance.setContent(pContent);
        return instance;
    }


    /**
     * Palauttaa kayttajan Id:n, jota ei saa vuotaa muille kayttajille.
     *
     * @return Kayttajan Id.
     */
    public final String getUserId() {
        return this.userId;
    }

    /**
     * Palauttaa salaisen kanavatunnuksen.
     *
     * @return Salainen kanavatunnus.
     */
    public final String getChannelId() {
        return this.channelId;
    }

    /**
     * Palauttaa viestin sisallon.
     *
     * @return Viestin sisalto.
     */
    public final String getContent() {
        return this.content;
    }

    /**
     * Asettaa kayttajan Id:n.
     *
     * @param pUserId Kayttajan Id.
     */
    public final void setUserId(final String pUserId) {
        this.userId = pUserId;
    }

    /**
     * Asettaa kanvatunnuksen.
     *
     * @param pChannelId Kanavatunnus.
     */
    public final void setChannelId(final String pChannelId) {
        this.channelId = pChannelId;
    }

    /**
     * Asettaa viestin sisallon.
     *
     * @param pContent Viestin sisalto.
     */
    public final void setContent(final String pContent) {
        this.content = pContent;
    }

}
