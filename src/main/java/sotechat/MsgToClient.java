package sotechat;

/**
 * Palvelimen asiakasohjelmalle lähettämä viesti on talletettu MsgToClient-olioon. Olion sisältö muokataan JSON-muotoon
 * Jackson-kirjaston avulla ennen kuin asiakasohjelma saa viestin.
 */
public class MsgToClient {

    private String userName;
    private String channelId;
    private String timeStamp;
    private String content;

    public MsgToClient(String userName, String channelId, String timeStamp, String content) {
        this.userName = userName;
        this.channelId = channelId;
        this.timeStamp = timeStamp;
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getTimeStamp() { return timeStamp; }

    public String getContent() { return content; }
}
