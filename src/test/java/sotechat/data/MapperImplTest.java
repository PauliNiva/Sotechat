package sotechat.data;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sotechat.repo.PersonRepo;

public class MapperImplTest {
    MapperImpl mapper = new MapperImpl();

    /*
    @Test
    public void testi() {
        mapper.
        Assert.assertEquals("UNKNOWN_USERNAME", mapper.getUsernameFromId("1"));
        Assert.assertEquals("hoitaja", mapper.getUsernameFromId("666"));
        Assert.assertTrue(mapper.isUserIdMapped("666"));
        Assert.assertEquals("UNKNOWN_ID", mapper.getIdFromRegisteredName("hoitsu"));
        Assert.assertEquals("666", mapper.getIdFromRegisteredName("hoitaja"));
        Assert.assertNotNull(mapper.getSecureRandomString());
    }
    */
}
