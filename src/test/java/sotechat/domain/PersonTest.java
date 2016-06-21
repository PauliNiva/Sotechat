package sotechat.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class PersonTest {

    private Person person;
    private String role = "USER_ADMIN";

    @Before
    public void setUp() {
        person = new Person();
    }

    @Test
    public void setAndGetUserNameTest() {
        person.setUserName("Pauli");
        Assert.assertEquals("Pauli", person.getUserName());
        person.setUserName("iluaP");
        Assert.assertEquals("iluaP", person.getUserName());
    }

    @Test
    public void setAndGetLoginNameTest() {
        person.setLoginName("Pauli");
        Assert.assertEquals("Pauli", person.getLoginName());
        person.setLoginName("iluaP");
        Assert.assertEquals("iluaP", person.getLoginName());
    }

    @Test
    public void setAndGetPasswordTest() {
        person.setPassword("0000");
        Assert.assertTrue(BCrypt.checkpw("0000", person.getPassword()));
    }

    @Test
    public void setAndGetRoleTest() {
        person.setRole(role);
        Assert.assertEquals(role, person.getRole());
    }

    @Test
    public void getSaltIsNotNullTest() {
        person.setPassword("0000");
        Assert.assertNotNull(person.getSalt());
    }
}
