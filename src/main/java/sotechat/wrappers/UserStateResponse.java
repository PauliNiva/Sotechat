package sotechat.wrappers;

import sotechat.data.Session;

/**
 * Luokka auttamaan JSONin paketoinnissa,
 * kun "tavalliselle kayttajalle" kerrotaan tila.
 */
public class UserStateResponse {

    /**
     * Tila ("start", "inpool" tai "chat").
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
     * Aihealue (esimerkiksi "mielenterveys").
     */
    private String category;

    /**
     * Salainen kanavatunnus.
     */
    private String channelId;

    /**
     * Konstruktori alustaa olion.
     *
     * @param session Kayttajan <code>Session</code>-olio.
     */
    public UserStateResponse(final Session session) {
        this.state = session.get("state");
        this.username = session.get("username");
        this.userId = session.get("userId");
        this.category = session.get("category");
        this.channelId = session.get("channelId");
    }

    /**
     * Palauttaa kayttajan tilan.
     *
     * @return Tila, jossa kayttaja on.
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
     * @return Aihealue johon käyttäjä kuuluu.
     */
    public final String getCategory() {
        return this.category;
    }

    /**
     * Palauttaa salaisen kanavatunnuksen.
     *
     * @return Salainen kanavatunnus.
     */
    public final String getChannelId() {
        return this.channelId;
    }

}
