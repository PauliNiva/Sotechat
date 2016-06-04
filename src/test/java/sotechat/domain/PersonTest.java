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
    public void setAndGetNameTest() {
        person.setName("Pauli");
        Assert.assertEquals("Pauli", person.getName());
        person.setName("iluaP");
        Assert.assertEquals("iluaP", person.getName());
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

    @Test
    public void setAndGetSaltTest() {
        person.setSalt("suola");
        Assert.assertEquals("suola", person.getSalt());
    }
}
