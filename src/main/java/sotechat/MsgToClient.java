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
        return this.userName;
    }

    public final String getChannelId() {
        return this.channelId;
    }

    public final String getTimeStamp() {
        return this.timeStamp;
    }

    public final String getContent() {
        return this.content;
    }
}
