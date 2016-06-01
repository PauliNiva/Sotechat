package sotechat.queue;

/**
 * Created by Asus on 1.6.2016.
 */
public class QueueItem {
    String channelId;
    String category;

    public QueueItem(String channelId, String category){
        this.channelId = channelId;
        this.category = category;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
