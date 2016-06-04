package sotechat;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class MsgToClientTest {

    /**
     * m2c.
     */
    private MsgToClient m2c;

    @Before
    public void setUp() {
        m2c = new MsgToClient("pniva", "1", "2", "cont");
    }

    /**
     * getUsername test.
     */
    @Test
    public void getUsernameWorks() {
        Assert.assertEquals("pniva", m2c.getUsername());
    }

    /**
     * getChannelId test.
     */
    @Test
    public void getChannelIdWorks() {
        Assert.assertEquals("1", m2c.getChannelId());
    }

    /**
     * getTimeStamp test.
     */
    @Test
    public void getTimeStampWorks() {
        Assert.assertEquals("2", m2c.getTimeStamp());
    }

    /**
     * getContent test.
     */
    @Test
    public void getContent() {
        Assert.assertEquals("cont", m2c.getContent());
    }
}
