package sotechat.wrappers;

/** Asiakasohjelman palvelimelle lahettama viesti paketoidaan MsgToServer-olion
 * sisalle, ennen kuin palvelimen ChatController-luokka voi kasitella sita.
 */
public class MsgToServer {

    /** Kayttajan yksiloiva salainen ID. */
    private String userId;
    /** Kanavan yksiloiva salainen ID. */
    private String channelId;
    /** Viestin sisalto. */
    private String content;



    /** Huom: Äla lisaa konstruktoria, se rikkoo Springin. */



    /** Palauttaa kayttajaID:n, jota ei saa vuotaa muille kayttajille.
     * @return Palauttaa kayttajaID:n, jota ei saa vuotaa muille kayttajille.
     */
    public final String getUserId() {
        return this.userId;
    }

    /** Palauttaa salaisen kanavaID:n.
     * @return Palauttaa salaisen kanavaID:n.
     */
    public final String getChannelId() {
        return this.channelId;
    }

    /** Palauttaa viestin sisallon.
     * @return Palauttaa viestin sisallon.
     */
    public final String getContent() {
        return this.content;
    }

    /** Setter.
     * @param pUserId userId
     */
    public final void setUserId(final String pUserId) {
        this.userId = pUserId;
    }

    /** Setter.
     * @param pChannelId channelId
     */
    public final void setChannelId(final String pChannelId) {
        this.channelId = pChannelId;
    }

    /** Setter.
     * @param pContent content
     */
    public final void setContent(final String pContent) {
        this.content = pContent;
    }


}
