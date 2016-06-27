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
 * Luokan tarkoituksena on "kovakoodata" kehitysvaiheen tietokantaan arvoja.
 */
@Configuration
@Profile("development")
public class DevProfile {

    /**
     * JPA-repositorio, joka hallinnoi henkiloita.
     */
    @Autowired
    private PersonRepo personRepo;

    /**
     * JPA-repositorio, joka hallinnoi keskusteluita.
     */
    @Autowired
    private ConversationRepo conversationRepo;

    /**
     * JPA-repositorio, joka hallinnoi viesteja.
     */
    @Autowired
    private MessageRepo messageRepo;

    /**
     * Mapper-olio, josta tassa kaytetaan siihen, etta sinne talletetaan
     * kirjautuneen kayttajan kayttajanimi ja kayttajaId siten, etta kayttaja
     * voidaan myohemmin hakea Mapper-oliosta kayttajaId:n perusteella.
     */
    @Autowired
    private Mapper mapper;

    /**
     * Luo kehitysvaiheen profiiliin kaksi kayttajaa valmiiksi, admin-kayttajan
     * ja yhden hoitaja-kayttajan.
     */
    @PostConstruct
    @Transactional
    public void init() {
        Person admin = new Person("admin");
        admin.setUserName("pauli");
        admin.setPassword("0000");
        admin.setLoginName("admin");
        admin.setUserId("admin");
        admin.setRole("ROLE_ADMIN");
        personRepo.save(admin);
        mapper.mapProUsernameToUserId(admin.getUserName(), admin.getUserId());

        Person pro = new Person("666");
        pro.setUserName("Hoitaja");
        pro.setPassword("salasana");
        pro.setLoginName("hoitaja");
        pro.setUserId("666");
        pro.setRole("ROLE_USER");
        personRepo.save(pro);
        mapper.mapProUsernameToUserId(pro.getUserName(), pro.getUserId());
    }
}
