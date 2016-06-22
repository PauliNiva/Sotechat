package sotechat.auth;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sotechat.Launcher;
import sotechat.data.Mapper;
import sotechat.repo.PersonRepo;
import sotechat.util.MockAuthentication;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@Transactional
@ActiveProfiles("development")
public class JpaAuthenticationProviderTest {

    @Autowired
    private Mapper mapper;

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    JpaAuthenticationProvider jpaAuthProv;

    @Test
    public void testi() {
        Authentication mockAuthentication = new MockAuthentication("admin", "0000");
        Assert.assertNotNull(jpaAuthProv.authenticate(mockAuthentication));
    }

    @Test(expected = AuthenticationException.class)
    public void testi2() {
        Authentication mockAuthentication = new MockAuthentication("admin", "0001");
        jpaAuthProv.authenticate(mockAuthentication);
    }

    @Test(expected = AuthenticationException.class)
    public void testi3() {
        Authentication mockAuthentication = new MockAuthentication("iluaP", "0000");
        jpaAuthProv.authenticate(mockAuthentication);
    }

    @Test
    public void testi4() {
        Authentication mockAuthentication = new MockAuthentication("hoitaja", "salasana");
        Assert.assertNotNull(jpaAuthProv.authenticate(mockAuthentication));
    }

    @Test
    public void supportsReturnTrue() {
        Assert.assertTrue(jpaAuthProv.supports(MockAuthentication.class));
    }
}
