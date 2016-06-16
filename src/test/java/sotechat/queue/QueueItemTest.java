package sotechat.queue;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sotechat.wrappers.QueueItem;

public class QueueItemTest {

    QueueItem item;

    @Before
    public void setUp(){
        item = new QueueItem("444", "mielenterveys", "Anon");
    }

    @Test
    public void getChannelIdTest(){
        Assert.assertEquals("444", item.getChannelId());
    }

    @Test
    public void getCategoryTest(){
        Assert.assertEquals("mielenterveys", item.getCategory());
    }

    @Test
    public void getUsernameTest(){
        Assert.assertEquals("Anon", item.getUsername());

    }

}
