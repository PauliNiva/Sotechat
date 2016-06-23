package sotechat.wrappers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by varkoi on 22.6.2016.
 */
public class ConvInfoTest {

    ConvInfo info;

    @Before
    public void setUp(){
        info = new ConvInfo("xxx", "30.1.", "Salla", "hammashoito");
    }

    @Test
    public void getchannelIdTest(){
        Assert.assertEquals("xxx", info.getChannelId());
    }

    @Test
    public void getDateTest(){
        Assert.assertEquals("30.1.", info.getDate());
    }

    @Test
    public void getPersonTest(){
        Assert.assertEquals("Salla", info.getPerson());
    }

    @Test
    public void getCategoryTest(){
        Assert.assertEquals("hammashoito", info.getCategory());
    }

}
