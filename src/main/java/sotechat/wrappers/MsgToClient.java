package sotechat.wrappers;

/**
 * Palvelimen asiakasohjelmalle lahettama viesti on talletettu
 * MsgToClient-olioon. Olion sisalto muokataan JSON-muotoon
 * Jackson-kirjaston avulla ennen kuin asiakasohjelma saa viestin.
 *
 * Syyt, miksi yksi Message-luokka ei riita:
 *  - Halutaan timeStampit serverin, ei clientin toimesta
 *  - Ei haluta vuotaa salaisia kayttajaID:t kaikille
 */
public class  MsgToClient implements Comparable<MsgToClient> {

    /**
     * Viestin Id.
     */
    private String messageId;

    /**
     * Julkinen kayttajanimi.
     */
    private String username;

    /**
     * Salainen kanavatunnus.
     */
    private String channelId;

    /**
     * Aikaleima. Arvoksi annetaan aika jolloin viesti saapuu palvelimelle.
     * Leiman muotona on <code>String</code> Angularia varten.
     */
    private String timeStamp;

    /**
     * Viestin sisalto.
     */
    private String content;

    /**
     * Konstruktori alustaa olion annetuilla parametreilla.
     *
     * @param pMessageId messageId
     * @param pUsername username
     * @param pChannelId channelId
     * @param pTimeStamp timeStamp
     * @param pContent content
     */
    public MsgToClient(final String pMessageId, final String pUsername,
                       final String pChannelId, final String pTimeStamp,
                       final String pContent) {
        this.messageId = pMessageId;
        this.username = pUsername;
        this.channelId = pChannelId;
        this.timeStamp = pTimeStamp;
        this.content = pContent;
    }

    /**
     * Palauttaa viestin Id:n.
     *
     * @return Viestin Id.
     */
    public final String getMessageId() {
        return this.messageId;
    }

    /**
     * Palauttaa kayttajanimen.
     *
     * @return Kayttajanimi.
     */
    public final String getUsername() {
        return this.username;
    }

    /**
     * Palauttaa kanavatunnuksen, jolla kanavaa voi kuunnella ja sinne voi
     * lahettaa viesteja.
     *
     * @return Kanavatunnus.
     */
    public final String getChannelId() {
        return this.channelId;
    }

    /**
     * Palauttaa aikaleiman.
     *
     * @return Aikaleima.
     */
    public final String getTimeStamp() {
        return this.timeStamp;
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
     * Vertaa oliota argumenttina annettuun toiseen
     * <code>MsgToClient</code>-olioon.
     *
     * @param other <code>MsgToClient</code>-olio johon verrataan.
     * @return palauttaa luvun -1 jos argumenttina
     * annetun olion <code>timeStamp</code> on suurempi. Jos
     * <code>timeStamp</code> on pienempi palauttaa luvun 1 ja jos ne ovat
     * yhtäsuuret, niin palautetaan 0.
     */
    public final int compareTo(final MsgToClient other) {
        int thistime = Integer.parseInt(this.timeStamp);
        int othertime = Integer.parseInt(other.getTimeStamp());
        if (thistime < othertime) {
            return -1;
        } else if (othertime < thistime) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Muodostaa <code>MsgToClient</code>-oliosta <code>String</code> esityksen.
     *
     * @return <code>MsgToClient</code>-olio merkkijonona.
     */
    @Override
    public String toString() {
        return "MessageID " + messageId + ", username " + username + ", "
                + channelId + ", timeStamp " + timeStamp + ", content " + content;
    }

}
