package sotechat;

/**
 * Luokan tarkoitus on auttaa JSONin paketoinnissa,
 * kun uusi client liitetään chattiin.
 */
public class JoinResponse {

    /** Julkinen käyttäjänimi. */
    private String userName;
    /** Salainen käyttäjäID. */
    private String userId;
    /** Salainen kanavaID. */
    private String channelId;

    /** Konstruktori alustaa olion parametreinä annetuilla arvoilla. */
    public JoinResponse(final String pUserName, final String pUserId,
                        final String pChannelId) {
        this.userName = pUserName;
        this.userId = pUserId;
        this.channelId = pChannelId;
    }

    /** Palauttaa julkisen käyttäjänimen. */
    public final String getUserName() {
        return this.userName;
    }

    /** Palauttaa salaisen käyttäjäID:n. */
    public final String getUserId() {
        return this.userId;
    }

    /** Palauttaa salaisen kanavaID:n. */
    public final String getChannelId() {
        return this.channelId;
    }
}
