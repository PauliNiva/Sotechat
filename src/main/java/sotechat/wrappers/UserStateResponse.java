package sotechat.wrappers;

import sotechat.data.Session;

/** Luokan tarkoitus on auttaa JSONin paketoinnissa,
 * kun "tavalliselle kayttajalle" kerrotaan state.
 */
public class UserStateResponse {

    /** Tila ("start", "inpool", "chat"). */
    private String state;
    /** Julkinen kayttajanimi. */
    private String username;
    /** Salainen kayttajaID. */
    private String userId;
    /** Kategoria (esim. "mielenterveys"). */
    private String category;
    /** Salainen kanavaID. */
    private String channelId;

    /** Konstruktori alustaa olion.
     * @param session oma session-olio
     */
    public UserStateResponse(
            final Session session
    ) {
        this.state = session.get("state");
        this.username = session.get("username");
        this.userId = session.get("userId");
        this.category = session.get("category");
        this.channelId = session.get("channelId");
    }

    /** Antaa tilan.
     * @return state.
     */
    public final String getState() {
        return this.state;
    }

    /** Palauttaa julkisen kayttajanimen.
     * @return Palauttaa julkisen kayttajanimen.
     */
    public final String getUsername() {
        return this.username;
    }

    /** Palauttaa salaisen kayttajaID:n.
     * @return Palauttaa salaisen kayttajaID:n.
     */
    public final String getUserId() {
        return this.userId;
    }

    /** Palauttaa kategorian, esim "mielenterveys".
     * @return Palauttaa kategorian.
     */
    public final String getCategory() {
        return this.category;
    }

    /** Palauttaa salaisen kanavaID:n.
     * @return Palauttaa salaisen kanavaID:n.
     */
    public final String getChannelId() {
        return this.channelId;
    }
}
