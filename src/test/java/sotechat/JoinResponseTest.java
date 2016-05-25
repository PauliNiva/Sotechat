package sotechat;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class JoinResponseTest {

    private JoinResponse response;

    /**
     * Before.
     */
    @Before
    public void setUp() {
        this.response = new JoinResponse("iluaP", "6", "9");
    }

    /**
     * getUserName test.
     */
    @Test
    public void getUserNameWorks() {
        Assert.assertEquals("iluaP", this.response.getUserName());
    }

    /**
     * getUserId test.
     */
    @Test
    public void getUserIdWorks() {
        Assert.assertEquals("6", this.response.getUserId());
    }

    /**
     * getChannelId test.
     */
    @Test
    public void getChannelIdWorks() {
        Assert.assertEquals("9", this.response.getChannelId());
    }
}
