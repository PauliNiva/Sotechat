package sotechat.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MapperTest {

    Mapper mapper;

    @Before
    public void setUp() {
        mapper = new Mapper();
    }

    @Test
    public void getIdFromRegisteredNameTest() {
        mapper.mapProUsernameToUserId("pauli", "admin");
        Assert.assertEquals("UNKNOWN_ID", mapper.getIdFromRegisteredName(""));
        Assert.assertEquals("UNKNOWN_ID", mapper.getIdFromRegisteredName(null));
        Assert.assertEquals("admin", mapper.getIdFromRegisteredName("pauli"));
        Assert.assertEquals("UNKNOWN_ID", mapper.getIdFromRegisteredName("iluap"));
    }
}
