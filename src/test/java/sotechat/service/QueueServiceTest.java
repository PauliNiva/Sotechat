package sotechat.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sotechat.data.Queue;
import sotechat.data.QueueItem;
import sotechat.data.QueueImpl;
import sotechat.websocketService.QueueService;

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
        first = new QueueItem("617", "paihdetyo", "jaakko");
        second = new QueueItem("331", "terveyskeskus", "kerttu");
        queue = new QueueImpl(first, second);
        service = new QueueService(queue);
    }

    @Test
    public void addToQueueTest(){
        boolean success = service.addToQueue("333", "neuvola", "heli");
        Assert.assertTrue(success);
        Assert.assertEquals(3, queue.length());
        Assert.assertEquals("333", queue.getQueue().get(2).getChannelId());
    }

    @Test
    public void addToQueueTest2(){
        boolean success = service.addToQueue("222", "hammashoito", "veikko");
        boolean success2 = service.addToQueue("111", "neuvola", "anu");
        Assert.assertTrue(success2);
        Assert.assertEquals(4, queue.length());
        Assert.assertEquals("anu", queue.getQueue().get(3).getUsername());
    }

    @Test
    public void addToQueueTest3(){
        for(int i=0; i<1000000; i++){
            String id = "123" + i;
            boolean success = service.addToQueue(id, "mielenterveys", "Anon");
            Assert.assertTrue(success);
        }
        Assert.assertEquals(1000002, queue.length());
    }

    @Test
    public void queueToStringTest(){
        String qstring = service.toString();
        String json = "{\"jono\": [{\"channelId\": \"617\", \"category\": "
            + "\"paihdetyo\", \"username\": \"jaakko\"}, {\"channelId\": "
            + "\"331\", \"category\": \"terveyskeskus\", \"username\": "
            + "\"kerttu\"}]}";
        Assert.assertEquals(json, qstring);
    }

    @Test
    public void queueToStringTest2(){
        service.addToQueue("224", "hammashoito", "teuvo");
        String qstring = service.toString();
        String json = "{\"jono\": [{\"channelId\": \"617\", \"category\": "
                + "\"paihdetyo\", \"username\": \"jaakko\"}, {\"channelId\": "
                + "\"331\", \"category\": \"terveyskeskus\", \"username\": "
                + "\"kerttu\"}, {\"channelId\": \"224\", \"category\": "
                + "\"hammashoito\", \"username\": \"teuvo\"}]}";
        Assert.assertEquals(json, qstring);
    }

    @Test
    public void firstOfQueueTest(){
        String first = service.firstOfQueue();
        String json = "{\"channelId\": \"617\", \"category\": "
                + "\"paihdetyo\", \"username\": \"jaakko\"}";
        Assert.assertEquals(json, first);
    }

    @Test
    public void firstOfQueueTest2(){
        String first = service.firstOfQueue();
        String second = service.firstOfQueue();
        String json = "{\"channelId\": \"331\", \"category\": "
                + "\"terveyskeskus\", \"username\": "
                + "\"kerttu\"}";
        Assert.assertEquals(json, second);
    }

    @Test
    public void firstOfCategoryTest(){
        String first = service.firstOfCategory("terveyskeskus");
        String json = "{\"channelId\": \"331\", \"category\": "
                + "\"terveyskeskus\", \"username\": "
                + "\"kerttu\"}";
        Assert.assertEquals(json, first);
    }

    @Test
    public void firstOfCategoryTest2(){
        service.addToQueue("221", "terveyskeskus", "hanna");
        String first = service.firstOfCategory("terveyskeskus");
        String json = "{\"channelId\": \"331\", \"category\": "
                + "\"terveyskeskus\", \"username\": "
                + "\"kerttu\"}";
        Assert.assertEquals(json, first);
    }

    @Test
    public void removeFromQueueTest(){
        String removed = service.removeFromQueue("331");
        String json = "{\"channelId\": \"331\", \"category\": "
                + "\"terveyskeskus\", \"username\": "
                + "\"kerttu\"}";
        Assert.assertEquals(json, removed);
    }

    @Test
    public void removeFromQueueTest2(){
        String removed = service.removeFromQueue("331");
        Assert.assertEquals(1, queue.length());
    }

    @Test
    public void removeFromQueueTest3(){
        for(int i=0; i<100000; i++){
            String id = "123" + i;
            service.addToQueue(id, "mielenterveys", "Anon");
        }
        String removed = service.removeFromQueue("12399998");
        String json = "{\"channelId\": \"12399998\", \"category\": "
                + "\"mielenterveys\", \"username\": "
                + "\"Anon\"}";
        Assert.assertEquals(json, removed);
        Assert.assertEquals(100001, queue.length());
    }

    @Test
    public void queueLengthTest(){
        Assert.assertEquals(2, service.queueLength());
    }

    @Test
    public void queueLengthTest2(){
        Assert.assertEquals(1, service.queueLength("331"));
    }

    @Test
    public void queueLengthTest3() {
        Assert.assertEquals(0, service.queueLength("331", "terveyskeskus"));
    }

    @Test
    public void queueLengthTest4(){
        service.addToQueue("228", "terveyskeskus", "pena");
        Assert.assertEquals(1, service.queueLength("228", "terveyskeskus"));
        service.addToQueue("882", "terveyskeskus", "japi");
        Assert.assertEquals(1, service.queueLength("228", "terveyskeskus"));
        Assert.assertEquals(2, service.queueLength("882", "terveyskeskus"));
    }

    @Test
    public void queueLengthTest5(){
        service.addToQueue("246", "hammashoito", "peetu");
        Assert.assertEquals(3, service.queueLength());
    }

}
