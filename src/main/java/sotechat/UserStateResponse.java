package sotechat;

/** Luokan tarkoitus on auttaa JSONin paketoinnissa,
 * kun "tavalliselle käyttäjälle" kerrotaan state.
 */
public class UserStateResponse {

    /** Tila ("start", "inpool", "chat"). */
    private String state;
    /** Julkinen käyttäjänimi. */
    private String username;
    /** Salainen käyttäjäID. */
    private String userId;
    /** Kategoria (esim. "mielenterveys"). */
    private String category;
    /** Salainen kanavaID. */
    private String channelId;

    /** Konstruktori alustaa olion.
     * @param pState state
     * @param pUsername username
     * @param pUserId userId
     * @param pCategory category
     * @param pChannelId channelId
     */
    public UserStateResponse(final String pState,
                             final String pUsername,
                             final String pUserId,
                             final String pCategory,
                             final String pChannelId) {
        this.state = pState;
        this.username = pUsername;
        this.userId = pUserId;
        this.category = pCategory;
        this.channelId = pChannelId;
    }

    /** Antaa tilan.
     * @return state.
     */
    public final String getState() {
        return this.state;
    }

    /** Palauttaa julkisen käyttäjänimen.
     * @return Palauttaa julkisen käyttäjänimen.
     */
    public final String getUsername() {
        return this.username;
    }

    /** Palauttaa salaisen käyttäjäID:n.
     * @return Palauttaa salaisen käyttäjäID:n.
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
