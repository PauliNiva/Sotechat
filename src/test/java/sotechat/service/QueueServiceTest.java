package sotechat.service;

import org.junit.Before;
import org.junit.Test;
import sotechat.queue.Queue;
import sotechat.queue.QueueItem;
import sotechat.service.QueueService;
import sotechat.queue.QueueImpl;

/**
 * Created by varkoi on 3.6.2016.
 */
public class QueueServiceTest {

    QueueService service;
    QueueItem first;
    QueueItem second;
    Queue queue;

    @Before
    public void setUp(){
        service = new QueueService(new QueueImpl(first, second));
    }

    @Test
    public void addToQueueTest(){
        service.addToQueue("333", "", "");
    }
}
