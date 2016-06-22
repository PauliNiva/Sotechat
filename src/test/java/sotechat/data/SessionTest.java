package sotechat.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SessionTest {

    Session session;

    @Before
    public void setUp() {
        session = new Session();
    }

    @Test
    public void removeChannelTest() {
        session.addChannel("666");
        Assert.assertTrue(session.hasAccessToChannel("666"));
        session.removeChannel("666");
        Assert.assertFalse(session.hasAccessToChannel("666"));
        Assert.assertFalse(session.isPro());
    }

    @Test
    public void isProTest() {
        Assert.assertFalse(session.isPro());
        session.set("state", "pro");
        Assert.assertTrue(session.isPro());
    }

}
