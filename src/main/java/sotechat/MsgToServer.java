package sotechat;

/**
 * Asiakasohjelman palvelimelle lähettämä viesti paketoidaan MsgToServer-olion
 * sisälle, ennen kuin palvelimen ChatController-luokka voi käsitellä sitä.
 */
public class MsgToServer {

    private String userId;
    private String channelId;
    private String content;

    public final String getUserId() {
        return this.userId;
    }

    public final String getChannelId() {
        return this.channelId;
    }

    public final String getContent() {
        return this.content;
    }
}
