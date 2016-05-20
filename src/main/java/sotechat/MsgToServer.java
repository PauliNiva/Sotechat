package sotechat;

/**
 * Asiakasohjelman palvelimelle lähettämä viesti paketoidaan MsgToServer-olion sisälle, ennen kuin palvelimen
 * ChatController-luokka voi käsitellä sitä.
 */
public class MsgToServer {

    private String userId;
    private String channelId;
    private String content;

    public String getUserId() {
        return userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getContent() { return content; }
}
