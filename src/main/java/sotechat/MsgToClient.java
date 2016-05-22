package sotechat;

/**
 * Palvelimen asiakasohjelmalle lähettämä viesti on talletettu
 * MsgToClient-olioon. Olion sisältö muokataan JSON-muotoon
 * Jackson-kirjaston avulla ennen kuin asiakasohjelma saa viestin.
 */
public class MsgToClient {

    private String userName;
    private String channelId;
    private String timeStamp;
    private String content;

    public MsgToClient(final String pUserName, final String pChannelId,
                       final String pTimeStamp, final String pContent) {
        this.userName = pUserName;
        this.channelId = pChannelId;
        this.timeStamp = pTimeStamp;
        this.content = pContent;
    }

    public final String getUserName() {
        return userName;
    }

    public final String getChannelId() {
        return channelId;
    }

    public final String getTimeStamp() {
        return timeStamp;
    }

    public final String getContent() {
        return content;
    }
}
