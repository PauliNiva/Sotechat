package sotechat.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MapperTest {

    Mapper mapper;

    @Before
    public void setUp() {
        mapper = new MapperImpl();
    }

    @Test
    public void getIdFromRegisteredNameTest() {
        mapper.mapProUsernameToUserId("pauli", "admin");
        Assert.assertEquals("admin", mapper.getIdFromRegisteredName("pauli"));
    }
}
