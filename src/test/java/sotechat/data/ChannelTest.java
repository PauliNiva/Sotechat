package sotechat.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ChannelTest {

    Session session;
    Channel channel;

    @Before
    public void setUp() {
        session = new Session();
        session.set("userId", "admin");
        channel = new Channel("666");
    }

    @Test
    public void AddSubscriberTest() {
        Assert.assertFalse(channel.hasActiveUser("admin"));
        Assert.assertFalse(channel.hasHistoricUser("admin"));
        channel.addSubscriber(session);
        Assert.assertTrue(channel.hasActiveUser("admin"));
        Assert.assertTrue(channel.hasHistoricUser("admin"));
        channel.removeActiveUserId("admin");
        Assert.assertFalse(channel.hasActiveUser("admin"));
        Assert.assertTrue(channel.hasHistoricUser("admin"));
    }

    @Test
    public void getHistoricUserIdsTest() {
        Assert.assertEquals(0, channel.getHistoricUserIds().size());
        channel.allowParticipation(session);
        Assert.assertEquals(1, channel.getHistoricUserIds().size());
    }
}
