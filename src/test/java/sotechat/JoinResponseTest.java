package sotechat;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class JoinResponseTest {

    private JoinResponse response;

    @Before
    public void setUp() {
        this.response = new JoinResponse("iluaP", "6", "9");
    }

    @Test
    public void getUserNameWorks() {
        Assert.assertEquals("iluaP", this.response.getUserName());
    }

    @Test
    public void getUserIdWorks() {
        Assert.assertEquals("6", this.response.getUserId());
    }

    @Test
    public void getChannelIdWorks() {
        Assert.assertEquals("9", this.response.getChannelId());
    }
}
