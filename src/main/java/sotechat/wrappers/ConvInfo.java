package sotechat.wrappers;

public class ConvInfo {

    private String channelId;
    private String date;
    private String person;
    private String category;

    public ConvInfo(){}

    public ConvInfo(final String channelId, final String date,
                    final String person, final String category){
        this.channelId = channelId;
        this.date = date;
        this.person = person;
        this.category = category;
    }

    public final String getChannelId() {
        return channelId;
    }

    public final String getDate() {
        return date;
    }

    public final String getPerson() {
        return person;
    }

    public String getCategory() {
        return category;
    }

}
