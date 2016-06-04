package sotechat.wrappers;

/** Asiakasohjelman palvelimelle lähettämä viesti paketoidaan MsgToServer-olion
 * sisälle, ennen kuin palvelimen ChatController-luokka voi käsitellä sitä.
 */
public class MsgToServer {

    /** Käyttäjän yksilöivä salainen ID. */
    private String userId;
    /** Kanavan yksilöivä salainen ID. */
    private String channelId;
    /** Viestin sisältö. */
    private String content;



    /** Huom: Älä lisää konstruktoria, se rikkoo Springin. */



    /** Palauttaa käyttäjäID:n, jota ei saa vuotaa muille käyttäjille.
     * @return Palauttaa käyttäjäID:n, jota ei saa vuotaa muille käyttäjille.
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

    /** Palauttaa viestin sisällön.
     * @return Palauttaa viestin sisällön.
     */
    public final String getContent() {
        return this.content;
    }
}
