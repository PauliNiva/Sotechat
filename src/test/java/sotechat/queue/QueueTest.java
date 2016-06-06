package sotechat.queue;

import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;
import sotechat.queue.Queue;
import sotechat.queue.QueueImpl;
import sotechat.queue.QueueItem;

import java.util.List;

public class QueueTest {

    QueueImpl queue;
    QueueItem first;
    QueueItem second;

    @Before
    public void setUp(){
        first = new QueueItem("444", "hammashoito", "jesse");
        second = new QueueItem("829", "neuvola", "eevi");
        this.queue = new QueueImpl(first, second);
    }

    @Test
    public void addToTest(){
        queue.addTo("324", "mielenterveys", "hanna h");
        Assert.assertEquals(3, queue.length());
        QueueItem item = queue.returnQueue().get(2);
        Assert.assertEquals("324", item.getChannelId());
        Assert.assertEquals("mielenterveys", item.getCategory());
        Assert.assertEquals("hanna h", item.getUsername());
    }

    @Test
    public void addToTest2(){
        queue.addTo("222", "hammashoito", "jokuvaan");
        queue.addTo("422", "paihdetyo", "jokutoinen");
        Assert.assertEquals(4, queue.length());
        QueueItem item1 = queue.returnQueue().get(2);
        QueueItem item2 = queue.returnQueue().get(3);
        Assert.assertEquals("222", item1.getChannelId());
        Assert.assertEquals("422", item2.getChannelId());
    }

    @Test
    public void pollFirstTest(){
        QueueItem item = queue.pollFirst();
        Assert.assertEquals("444", item.getChannelId());
    }

    @Test
    public void pollFirstTest2(){
        QueueItem item = queue.pollFirst();
        QueueItem item2 = queue.pollFirst();
        Assert.assertNotEquals("hammashoito", item2.getCategory());
        Assert.assertEquals("eevi", item2.getUsername());
    }

    @Test
    public void pollFirstFromTest(){
        QueueItem item = queue.pollFirstFrom("neuvola");
        Assert.assertEquals("829", item.getChannelId());
        Assert.assertEquals(1, queue.length());
        Assert.assertEquals("jesse", queue.returnQueue().get(0).getUsername());
    }

    @Test
    public void pollFirstFromTest2(){
        queue.addTo("621", "neuvola", "jaana");
        QueueItem item = queue.pollFirstFrom("neuvola");
        Assert.assertNotEquals("jaana", item.getUsername());
        Assert.assertEquals("829", item.getChannelId());
    }

    @Test
    public void removeTest(){
        QueueItem item = queue.remove("829");
        Assert.assertEquals(1, queue.length());
        Assert.assertEquals("eevi", item.getUsername());
        Assert.assertEquals("neuvola", item.getCategory());
    }

    @Test
    public void removeTest2(){
        QueueItem item1 = queue.remove("444");
        QueueItem item2 = queue.remove("829");
        Assert.assertEquals(0, queue.length());
        Assert.assertTrue(queue.returnQueue().isEmpty());
    }

    @Test
    public void lengthTest(){
        Assert.assertEquals(2, queue.length());
    }

    @Test
    public void lengthTest2(){
        queue.addTo("111", "terveyskeskus", "eetu");
        Assert.assertEquals(3, queue.length());
    }

    @Test
    public void itemsBeforeTest(){
        Assert.assertEquals(1, queue.itemsBefore("829"));
        Assert.assertEquals(0, queue.itemsBefore("444"));
    }

    @Test
    public void itemsBeforeTest2(){
        queue.addTo("776", "neuvola", "heli");
        Assert.assertEquals(2, queue.itemsBefore("776"));
    }

    @Test
    public void itemsBeforeInTest(){
        Assert.assertEquals(0, queue.itemsBeforeIn("829", "neuvola"));
    }

    @Test
    public void itemsBeforeInTest2(){
        queue.addTo("776", "neuvola", "heli");
        Assert.assertEquals(1, queue.itemsBeforeIn("776", "neuvola"));
    }

    @Test
    public void returnQueueTest(){
        List<QueueItem> items = queue.returnQueue();
        Assert.assertEquals(items.get(0), first);
        Assert.assertEquals(items.get(1), second);
    }

    @Test
    public void returnQueueTest2(){
        QueueImpl sq = new QueueImpl();
        Assert.assertTrue(sq.returnQueue().isEmpty());
    }

}
