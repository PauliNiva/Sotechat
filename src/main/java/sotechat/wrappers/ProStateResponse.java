package sotechat.wrappers;

/** Luokan tarkoitus on auttaa JSONin paketoinnissa,
 * kun "tavalliselle kayttajalle" kerrotaan state.
 */
public class ProStateResponse {

    /** Tila ("start", "inpool", "chat"). */
    private String state;

    /** Julkinen kayttajanimi. */
    private String username;

    /** Salainen kayttajaID. */
    private String userId;

    /** WebSocket-osoite, johon subscribaamalla saa jonon paivitykset. */
    private String QBCC;

    /** Onko kayttaja merkinnyt itsensa paikallaolevaksi.
     * "true" tai "false" Stringina JSON paketointia varten. */
    private String online;

    /** Salainen kanavaID. */
    private String channelIds;

    /** Konstruktori alustaa olion.
     * @param pState state
     * @param pUsername username
     * @param pUserId userId
     * @param pQBCC QBCC
     * @param pOnline online
     * @param pChannelIds channelId
     */
    public ProStateResponse(final String pState, final String pUsername,
                            final String pUserId, final String pQBCC,
                            final String pOnline, final String pChannelIds) {
        this.state = pState;
        this.username = pUsername;
        this.userId = pUserId;
        this.QBCC = pQBCC;
        this.online = pOnline;
        this.channelIds = pChannelIds;
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
    public final String getQBCC() {
        return this.QBCC;
    }

    /** getteri online-statukselle.
     * @return "true" tai "false".
     */
    public final String getOnline() {
        return this.online;
    }

    /** Palauttaa salaisen kanavaID:n.
     * @return Palauttaa salaisen kanavaID:n.
     */
    public final String getChannelIds() {
        return this.channelIds;
    }
}