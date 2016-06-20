package sotechat.profiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;

import javax.annotation.PostConstruct;

@Configuration
@Profile("development")
public class DevProfile {

    @Autowired
    PersonRepo personRepo;

    @Autowired
    ConversationRepo conversationRepo;

    @Autowired
    MessageRepo messageRepo;

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

        Person hoitaja = new Person("666");
        hoitaja.setUserName("Hoitaja");
        hoitaja.setPassword("salasana");
        hoitaja.setLoginName("hoitaja");
        hoitaja.setUserId("666");
        hoitaja.setRole("ROLE_USER");
        personRepo.save(hoitaja);

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
    }
}
