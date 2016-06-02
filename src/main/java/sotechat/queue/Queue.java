package sotechat.queue;

import java.util.List;

/**
 * Created by varkoi on 2.6.2016.
 */
public interface Queue {
    void addTo(String channelId,
               String category,
               String username);

    QueueItem getFirst();

    QueueItem getFirstFrom(String category);

    QueueItem remove(String channelId);

    int length();

    int itemsBefore(String channelId);

    List<QueueItem> returnQueue();
}
