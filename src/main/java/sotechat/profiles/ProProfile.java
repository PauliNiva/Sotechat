package sotechat.profiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import sotechat.data.Mapper;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;

import javax.annotation.PostConstruct;

/**
 * Tuotantoprofiili.
 */
@Configuration
@Profile("production")
public class ProProfile {

    /**
     * <code>JPA</code>-sailo, joka sailoo <code>person</code>-olioita.
     */
    @Autowired
    private PersonRepo personRepo;

    /**
     * <code>Mapper</code>-olio, jonne talletetaan kirjautuneen kayttajan
     * <code>username</code> ja <code>userId</code>, jotta kayttaja voidaan
     * hakea <code>Mapper</code>-oliosta <code>userId</code>:n perusteella.
     */
    @Autowired
    private Mapper mapper;

    /**
     * Ymparisto.
     */
    @Autowired
    private Environment env;

    /**
     * Luo kaksi kayttajaa valmiiksi, joista toisella on rooli "ADMIN" ja
     * toisella rooli "USER".
     */
    @PostConstruct
    @Transactional
    public void init() {
        String user = env.getRequiredProperty("sotechat-admin");
        String loginname = user.split(":")[0];
        String password = user.split(":")[1];
        if (personRepo.findByLoginName(loginname) == null) {
            Person admin = new Person(loginname);
            admin.setUserName(loginname);
            admin.hashPasswordWithSalt(password);
            admin.setLoginName(loginname);
            admin.setUserId(mapper.generateNewId());
            admin.setRole("ROLE_ADMIN");
            personRepo.save(admin);
            mapper.mapProUsernameToUserId(admin.getUserName(),
                    admin.getUserId());
        }
    }

}
