package sotechat.wrappers;

/**
 * Created by Asus on 19.6.2016.
 */
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

    public final void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public final void setDate(String date) {
        this.date = date;
    }

    public final void setPerson(String info) {
        this.person = person;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
