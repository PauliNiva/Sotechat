package sotechat.wrappers;

import sotechat.data.Session;

import static sotechat.service.StateService.QUEUE_BROADCAST_CHANNEL;

/** Luokan tarkoitus on auttaa JSONin paketoinnissa,
 * kun "ammattilaiskayttajalle" kerrotaan state.
 */
public class ProStateResponse {

    /** Aina "pro". */
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

    /** Konstruktori asettaa arvoiksi staattisia arvoja
     * seka session-oliosta kaivettuja arvoja.
     * @param session oma session-olio
     */
    public ProStateResponse(
            final Session session
    ) {
        this.state = "pro";
        this.username = session.get("username");
        this.userId = session.get("userId");
        this.QBCC = QUEUE_BROADCAST_CHANNEL;
        this.online = "true"; //TODO: session.get("online");
        this.channelIds = session.get("channelIds");
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
