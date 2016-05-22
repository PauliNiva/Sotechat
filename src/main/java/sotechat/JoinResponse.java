package sotechat;

public class JoinResponse {

    private String userName;
    private String userId;
    private String channelId;

    public JoinResponse(final String pUserName, final String pUserId,
                        final String pChannelId) {
        this.userName = pUserName;
        this.userId = pUserId;
        this.channelId = pChannelId;
    }

    public final String getUserName() {
        return this.userName;
    }

    public final String getUserId() {
        return this.userId;
    }

    public final String getChannelId() {
        return this.channelId;
    }
}
