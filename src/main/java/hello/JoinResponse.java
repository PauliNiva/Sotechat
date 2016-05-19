package hello;

/**
 * Created by leokallo on 19.5.2016.
 */
public class JoinResponse {

    private String userName;
    private String userId;
    private String channelId;

    public JoinResponse(String userName, String userId, String channelId) {
        this.userName = userName;
        this.userId = userId;
        this.channelId = channelId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() { return userId; }

    public String getChannelId() { return channelId; }
}
