package sotechat.profiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import sotechat.data.Mapper;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;

import javax.annotation.PostConstruct;

/**
 * Kehitysprofiili.
 */
@Configuration
@Profile("development")
public class DevProfile {

    /**
     * <code>JPA</code>-sailo, joka sailoo <code>person</code>-olioita.
     */
    @Autowired
    private PersonRepo personRepo;

    /**
     * <code>JPA</code>-sailo, joka sailoo <code>Conversation</code>-olioita.
     */
    @Autowired
    private ConversationRepo conversationRepo;

    /**
     * <code>JPA</code>-sailo, joka sailoo <code>Message</code>-olioita.
     */
    @Autowired
    private MessageRepo messageRepo;

    /**
     * <code>Mapper</code>-olio, jonne talletetaan kirjautuneen kayttajan
     * <code>username</code> ja <code>userId</code>, jotta kayttaja voidaan
     * hakea <code>Mapper</code>-oliosta <code>userId</code>:n perusteella.
     */
    @Autowired
    private Mapper mapper;

    /**
     * Luo kaksi kayttajaa valmiiksi, joista toisella on rooli "ADMIN" ja
     * toisella rooli "USER".
     */
    @PostConstruct
    @Transactional
    public void init() {
        Person admin = new Person("admin");
        admin.setUserName("pauli");
        admin.hashPasswordWithSalt("0000");
        admin.setLoginName("admin");
        admin.setUserId("admin");
        admin.setRole("ROLE_ADMIN");
        personRepo.save(admin);
        mapper.mapProUsernameToUserId(admin.getUserName(), admin.getUserId());

        Person pro = new Person("666");
        pro.setUserName("Hoitaja");
        pro.hashPasswordWithSalt("salasana");
        pro.setLoginName("hoitaja");
        pro.setUserId("666");
        pro.setRole("ROLE_USER");
        personRepo.save(pro);
        mapper.mapProUsernameToUserId(pro.getUserName(), pro.getUserId());
    }

}
