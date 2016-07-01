package sotechat.wrappers;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class MsgToClientTest {

    /**
     * m2c.
     */
    private MsgToClient m2c;
    private MsgToClient m2c1;
    private MsgToClient m2c2;
    private MsgToClient m2c3;

    @Before
    public void setUp() {
        m2c = new MsgToClient("1", "pniva", "1", "2", "cont");
        m2c1 = new MsgToClient("1", "pniva", "1", "1", "cont");
        m2c2 = new MsgToClient("1", "pniva", "1", "2", "cont");
        m2c3 = new MsgToClient("1", "pniva", "1", "3", "cont");
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

    @Test
    public void getMessageId() {
        Assert.assertEquals("1", m2c.getMessageId());
    }

    @Test
    public void compareTo() {
        Assert.assertEquals(0, m2c.compareTo(m2c2));
        Assert.assertEquals(-1, m2c.compareTo(m2c3));
        Assert.assertEquals(1, m2c.compareTo(m2c1));
    }

    @Test
    public void toStringTest() {
        String expected = "MessageID 1, username pniva, 1, timeStamp 2, content cont";
        Assert.assertEquals(expected, m2c.toString());
    }
}
