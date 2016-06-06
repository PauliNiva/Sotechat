package sotechat.wrappers;

/** Palvelimen asiakasohjelmalle lahettama viesti on talletettu
 * MsgToClient-olioon. Olion sisalto muokataan JSON-muotoon
 * Jackson-kirjaston avulla ennen kuin asiakasohjelma saa viestin.
 *
 * Syyt, miksi yksi Message-luokka ei riita:
 *  - Halutaan timeStampit serverin, ei clientin toimesta
 *  - Ei haluta vuotaa salaisia kayttajaID:t kaikille
 */
public class MsgToClient {

    /** Julkinen nimimerkki, kuten "Anon" tai "Hoitaja Anne". */
    private String username;
    /** Salainen avain kanavan kuunteluun ja viestien lahettamiseen. */
    private String channelId;
    /** Serverin aika viestin saapumiselle
     *  erityisessa muodossa AngularJS varten. */
    private String timeStamp;
    /** Viestin sisalto. */
    private String content;

    /** Konstruktori alustaa olion annetuilla parametreilla.
     * @param pUsername username
     * @param pChannelId channelId
     * @param pTimeStamp timeStamp
     * @param pContent content
     */
    public MsgToClient(final String pUsername, final String pChannelId,
                       final String pTimeStamp, final String pContent) {
        this.username = pUsername;
        this.channelId = pChannelId;
        this.timeStamp = pTimeStamp;
        this.content = pContent;
    }

    /** Palauttaa julkisen nimimerkin, kuten "Anon" tai "Hoitaja Anne".
     * @return Palauttaa julkisen nimimerkin, kuten "Anon" tai "Hoitaja Anne".
     */
    public final String getUsername() {
        return this.username;
    }

    /** Palauttaa salaisen avaimen, jolla kanavaa voi kuunnella + viestittaa.
     * @return Palauttaa salaisen avaimen.
     */
    public final String getChannelId() {
        return this.channelId;
    }

    /** Palauttaa ajan viestin saapumiselle (serverin ajassa).
     * @return Palauttaa ajan viestin saapumiselle (serverin ajassa).
     */
    public final String getTimeStamp() {
        return this.timeStamp;
    }

    /** Palauttaa viestin sisallon.
     * @return Palauttaa viestin sisallon.
     */
    public final String getContent() {
        return this.content;
    }
}
