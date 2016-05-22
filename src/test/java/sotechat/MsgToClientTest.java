package sotechat;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class MsgToClientTest {

    private MsgToClient m2c;

    @Before
    public void setUp() {
        m2c = new MsgToClient("pniva", "1", "2", "cont");
    }

    @Test
    public void getUserNameWorks() {
        Assert.assertEquals("pniva", m2c.getUserName());
    }

    @Test
    public void getChannelIdWorks() {
        Assert.assertEquals("1", m2c.getChannelId());
    }

    @Test
    public void getTimeStampWorks() {
        Assert.assertEquals("2", m2c.getTimeStamp());
    }

    @Test
    public void getContent() {
        Assert.assertEquals("cont", m2c.getContent());
    }
}
