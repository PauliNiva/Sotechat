package sotechat.queue;

/**
 * Created by Asus on 1.6.2016.
 */
public class Queue {
    List<QueueItem> queue;

    public Queue() {
        this.queue = new LinkedList<QueueItem>();
    }

    public final void addToQueue(final String channelId, String category) {
        this.queue.add(new QueueItem(channelId, category));
    }

    public final QueueItem getFirst() {
        return this.queue.pollFirst();
    }

    public final QueueItem getFirstFrom(final String category) {
        ListIterator<QueueItem> iterator = queue.listIterator(0);
        while (iterator.hasNext()) {
            QueueItem next = iterator.next();
            if (next.getCategory().equals(category)) {
                iterator.remove();
                return next;
            }
        }
        return null;
    }

    public final QueueItem remove(final String channelId){
        ListIterator<QueueItem> iterator = queue.listIterator(0);
        while(iterator.hasNext()){
            QueueItem next = iterator.next();
            if(next.getChannelId().equals(channelId)){
                iterator.remove();
                return next;
            }
        }
        return null;
    }

    public final int length(){
        return queue.size();
    }

    public final int itemsBefore(final String channelId){
        ListIterator<QueueItem> iterator = queue.listIterator(0);
        int count = 0;
        while(iterator.hasNext()){
            QueueItem next = iterator.next();
            if(next.getChannelId().equals(channelId)){
                return count;
            }
            count++;
        }
        return count;
    }

}
