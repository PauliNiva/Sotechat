package sotechat.data;

import org.junit.Assert;
import org.junit.Test;

public class MapperImplTest {
    MapperImpl mapper = new MapperImpl();

    @Test
    public void testi() {
        Assert.assertEquals("UNKNOWN_USERNAME", mapper.getUsernameFromId("1"));
        Assert.assertEquals("Hoitaja", mapper.getUsernameFromId("666"));
        Assert.assertTrue(mapper.isUserIdMapped("666"));
        Assert.assertEquals("UNKNOWN_ID", mapper.getIdFromRegisteredName("hoitsu"));
        Assert.assertEquals("666", mapper.getIdFromRegisteredName("Hoitaja"));
        Assert.assertNotNull(mapper.getSecureRandomString());
    }
}
