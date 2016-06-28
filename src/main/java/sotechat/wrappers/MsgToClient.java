package sotechat.wrappers;

/** Palvelimen asiakasohjelmalle lahettama viesti on talletettu
 * MsgToClient-olioon. Olion sisalto muokataan JSON-muotoon
 * Jackson-kirjaston avulla ennen kuin asiakasohjelma saa viestin.
 *
 * Syyt, miksi yksi Message-luokka ei riita:
 *  - Halutaan timeStampit serverin, ei clientin toimesta
 *  - Ei haluta vuotaa salaisia kayttajaID:t kaikille
 */
public class  MsgToClient implements Comparable<MsgToClient> {

    /** Viestin ID. */
    private String messageId;
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
     * @param pMessageId messageId
     * @param pUsername username
     * @param pChannelId channelId
     * @param pTimeStamp timeStamp
     * @param pContent content
     */
    public MsgToClient(
            final String pMessageId,
            final String pUsername,
            final String pChannelId,
            final String pTimeStamp,
            final String pContent
    ) {
        this.messageId = pMessageId;
        this.username = pUsername;
        this.channelId = pChannelId;
        this.timeStamp = pTimeStamp;
        this.content = pContent;
    }

    /** Palauttaa viestin Id:n.
     * @return Palauttaa viestin id:n.
     */
    public final String getMessageId() {
        return this.messageId;
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

    /**
     * Vertaa oliota parametrina annettuun toiseen MsgToClient olioon ja
     * palauttaa -1 jos, parametrina annetun olion timeStamp on isompi kuin
     * taman olion, 1 jos pienempi ja 0 jos ne ovat samaa suuruiset.
     * @param other MsgToClient olio johon verrataan
     * @return -1, 1 tai 0
     */
    public final int compareTo(MsgToClient other) {
        int thistime = Integer.parseInt(this.timeStamp);
        int othertime = Integer.parseInt(other.getTimeStamp());
        if(thistime<othertime) return -1;
        else if(othertime<thistime) return 1;
        else return 0;
    }

    @Override
    public String toString() {
        return "MessageID " + messageId + ", username " + username + ", "
                + channelId + ", timeStamp " + timeStamp + ", content " + content;
    }
}
