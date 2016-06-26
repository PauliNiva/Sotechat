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
import javax.annotation.PreDestroy;

@Configuration
@Profile("development")
public class DevProfile {

    @Autowired
    PersonRepo personRepo;

    @Autowired
    ConversationRepo conversationRepo;

    @Autowired
    MessageRepo messageRepo;

    @Autowired
    Mapper mapper;

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




    /* TODO: Saako poistaa?
        Conversation conversation = new Conversation("007", "6.6.2016");
        conversation.setCategory("Hammashoito");
        conversation.addPersonToConversation(admin);
        conversationRepo.save(conversation);

        admin.addConversationToPerson(conversation);
        personRepo.save(admin);

        Message message = new Message();
        message.setChannelId("007");
        message.setContent("This project sux ballz!");
        message.setDate("6.6.2016");
        message.setSender("pauli");
        message.setConversation(conversation);
        messageRepo.save(message);

        conversation.addMessageToConversation(message);
        conversationRepo.save(conversation);
        */
}
