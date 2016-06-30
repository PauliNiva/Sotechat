package sotechat.wrappers;

import sotechat.data.Session;

/**
 * Luokka auttamaan <code>JSON</code>:in paketoinnissa,
 * kun "ammattilaiskayttajalle" kerrotaan tila.
 */
public class ProStateResponse {

    /**
     * Ammattilaiskayttajan tila, joka on aina "pro".
     */
    private String state;

    /**
     * Julkinen kayttajanimi.
     */
    private String username;

    /**
     * Salainen kayttajatunnus.
     */
    private String userId;

    /**
     * <code>WebSocket</code>-osoite, jonka tilaamalla saa jonon paivitykset.
     */
    private String QBCC;

    /**
     * Onko kayttaja merkinnyt itsensa paikallaolevaksi. Arvona on joko
     * <code>true</code> tai <code>false</code>.
     */
    private String online;

    /**
     * Salainen kanavatunnus.
     */
    private String channelIds;

    /**
     * Konstruktori asettaa arvoiksi staattisia arvoja seka
     * <code>Session</code>-oliosta saatuja arvoja.
     *
     * @param session Ammattilaisen <code>Session</code>-olio.
     */
    public ProStateResponse(final Session session) {
        this.state = "pro";
        this.username = session.get("username");
        this.userId = session.get("userId");
        this.QBCC = "QBCC";
        this.online = session.get("online");
        this.channelIds = session.get("channelIds");
    }

    /**
     * Palauttaa ammattilaisen tilan.
     *
     * @return Ammattilainsen tila.
     */
    public final String getState() {
        return this.state;
    }

    /**
     * Palauttaa julkisen kayttajanimen.
     *
     * @return Julkinen kayttajanimi.
     */
    public final String getUsername() {
        return this.username;
    }

    /**
     * Palauttaa salaisen kayttajatunnuksen.
     *
     * @return Salainen kayttajatunnus.
     */
    public final String getUserId() {
        return this.userId;
    }

    /**
     * Palauttaa aihealueen.
     *
     * @return Aihealue.
     */
    public final String getQBCC() {
        return this.QBCC;
    }

    /**
     * Hakee tiedon siitä onko kayttaja merkinnyt itsensa paikalla olevaksi
     * vai ei.
     *
     * @return <code>true</code> jos paikalla ja <code>false</code> jos taas ei.
     */
    public final String getOnline() {
        return this.online;
    }

    /**
     * Palauttaa salaisen kanavatunnuksen.
     *
     * @return Salainen kanavatunnus.
     */
    public final String getChannelIds() {
        return this.channelIds;
    }

}
