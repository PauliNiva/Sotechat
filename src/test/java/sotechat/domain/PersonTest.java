package sotechat.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class PersonTest {

    private Person person;

    @Before
    public void setUp() {
        person = new Person();
    }

    @Test
    public void setAndGetScreenNameTest() {
        person.setScreenName("Pauli");
        Assert.assertEquals("Pauli", person.getScreenName());
        person.setScreenName("iluaP");
        Assert.assertEquals("iluaP", person.getScreenName());
    }

    @Test
    public void setAndGetUsernameTest() {
        person.setUsername("Pauli");
        Assert.assertEquals("Pauli", person.getUsername());
        person.setUsername("iluaP");
        Assert.assertEquals("iluaP", person.getUsername());
    }

    @Test
    public void setAndGetPasswordTest() {
        person.setPassword("0000");
        Assert.assertTrue(BCrypt.checkpw("0000", person.getPassword()));
    }
    
}
