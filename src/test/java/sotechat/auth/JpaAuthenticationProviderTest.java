package sotechat.auth;

import groovy.transform.TailRecursive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import sotechat.Launcher;
import sotechat.data.Mapper;
import sotechat.data.MapperImpl;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;
import sotechat.util.MockAuthentication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@javax.transaction.Transactional
@ActiveProfiles("development")
public class JpaAuthenticationProviderTest {

    @Autowired
    private Mapper mapper;

    @Autowired
    private PersonRepo personRepo;

    Authentication mockAuthentication = new MockAuthentication();

    @Autowired
    JpaAuthenticationProvider jpaAuthProv;

    @Test
    public void testi() {
        System.out.println(mockAuthentication.getPrincipal());
        System.out.println(mockAuthentication.getCredentials());
        jpaAuthProv.authenticate(mockAuthentication);
    }


}
