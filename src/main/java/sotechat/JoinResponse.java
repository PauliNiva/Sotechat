package sotechat;

/**
 * Luokan tarkoitus on auttaa JSONin paketoinnissa,
 * kun uusi client liitetään chattiin.
 */
public class JoinResponse {

    /** Julkinen käyttäjänimi. */
    private String userName;
    /** Salainen käyttäjäID. TODO: korvautuu sessionID:llä */

    /** Salainen kanavaID. */
    private String channelId;

    /**
     * Konstruktori alustaa olion parametreinä annetuilla arvoilla.
     * @param pUserName userName
     * @param pChannelId channelId
     */
    public JoinResponse(final String pUserName,
                        final String pChannelId) {
        this.userName = pUserName;
        this.channelId = pChannelId;
    }

    /**
     * @return Palauttaa julkisen käyttäjänimen.
     */
    public final String getUserName() {
        return this.userName;
    }

    /**
     * @return Palauttaa salaisen käyttäjäID:n. TODO: korvautuu sessionID:llä
     */

    /**
     * @return Palauttaa salaisen kanavaID:n.
     */
    public final String getChannelId() {
        return this.channelId;
    }
}
