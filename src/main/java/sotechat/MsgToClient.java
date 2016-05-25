package sotechat;

/**
 * Palvelimen asiakasohjelmalle lähettämä viesti on talletettu
 * MsgToClient-olioon. Olion sisältö muokataan JSON-muotoon
 * Jackson-kirjaston avulla ennen kuin asiakasohjelma saa viestin.
 *
 * Syyt, miksi yksi Message-luokka ei riitä:
 *  - Halutaan timeStampit serverin, ei clientin toimesta
 *  - Ei haluta vuotaa salaisia käyttäjäID:t kaikille
 */
public class MsgToClient {

    /** Julkinen nimimerkki, kuten "Anon" tai "Hoitaja Anne". */
    private String userName;
    /** Salainen avain kanavan kuunteluun ja viestien lähettämiseen. */
    private String channelId;
    /** Serverin aika viestin saapumiselle
     *  erityisessä muodossa AngularJS varten. */
    private String timeStamp;
    /** Viestin sisältö. */
    private String content;

    /**
     * Konstruktori alustaa olion annetuilla parametreillä.
     * @param pUserName userName
     * @param pChannelId channelId
     * @param pTimeStamp timeStamp
     * @param pContent content
     */
    public MsgToClient(final String pUserName, final String pChannelId,
                       final String pTimeStamp, final String pContent) {
        this.userName = pUserName;
        this.channelId = pChannelId;
        this.timeStamp = pTimeStamp;
        this.content = pContent;
    }

    /**
     * @return Palauttaa julkisen nimimerkin, kuten "Anon" tai "Hoitaja Anne".
     */
    public final String getUserName() {
        return this.userName;
    }

    /**
     * @return Palauttaa salaisen avaimen,
     * jolla kanavaa voi kuunnella + viestittää.
     */
    public final String getChannelId() {
        return this.channelId;
    }

    /**
     * @return Palauttaa ajan viestin saapumiselle (serverin ajassa).
     */
    public final String getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * @return Palauttaa viestin sisällön.
     */
    public final String getContent() {
        return this.content;
    }
}
