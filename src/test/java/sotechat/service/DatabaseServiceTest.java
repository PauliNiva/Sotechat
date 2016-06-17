package sotechat.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sotechat.Launcher;
import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.PersonRepo;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by varkoi on 14.6.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@Transactional
@ActiveProfiles("development")
public class DatabaseServiceTest {

    Person person;

    Conversation conversation;

    @Autowired
    PersonRepo personRepo;

    @Autowired
    ConversationRepo conversationRepo;

    @Autowired
    DatabaseService databaseService;

    @Before
    public void setUp() throws Exception {
        person = new Person("xxd");
        personRepo.save(person);
        conversation = new Conversation();
        conversation.setChannelId("xyzo");
        conversation.setDate("2006");
    }

    @Test
    @Transactional
    public void createConversationTest() throws Exception {
        databaseService.createConversation("Anon", "888a", "hammashoito");
        Assert.assertEquals("Conversation", conversationRepo.findAll().get(0).getClass().getSimpleName());
        Assert.assertNotNull(conversationRepo.findOne("888a"));
        Assert.assertNotNull(conversationRepo.findOne("888a").getDate());
    }

    @Test
    @Transactional
    public void createConversationTest2() throws Exception {
        databaseService.createConversation("Anon", "888b", "hammashoito");
        Assert.assertEquals("hammashoito", conversationRepo.findOne("888b").getCategory());
    }

    @Test
    @Transactional
    public void createConversationTest3() throws Exception {
        databaseService.createConversation("Anon", "888c", "hammashoito");
        Assert.assertEquals("888c", conversationRepo.findAll().get(0).getChannelId());
    }

    @Test
    @Transactional
    public void addPersonToConversationTest() throws Exception {
        conversationRepo.save(conversation);
        databaseService.addPersonToConversation("xxd", "xyzo");
        Assert.assertEquals("xyzo", personRepo.findOne("xxd").getConversationsOfPerson().get(0).getChannelId());
   }

    @Test
    @Transactional
    public void addPersonToConversationTest2() throws Exception {
        conversationRepo.save(conversation);
        databaseService.addPersonToConversation("xxd", "xyzo");
        Assert.assertEquals("xxd", conversationRepo.findOne("xyzo").getParticipantsOfConversation().get(0).getUserId());
    }

    @Test
    @Transactional
    public void saveMsgToDatabaseTest() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("xxx");
        conversationRepo.save(conversation);
        databaseService.saveMsgToDatabase("Salla", "Hoi", "23.4.2005", "224r");
        Assert.assertNotNull(conversationRepo.findOne("224r"));
        Assert.assertNotNull(conversationRepo.findOne("224r").getMessagesOfConversation().get(0));
    }

    @Test
    @Transactional
    public void saveMsgToDatabaseTest2() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("xxx");
        conversationRepo.save(conversation);
        databaseService.saveMsgToDatabase("Salla", "Hoi", "23.4.2005", "224r");
        Assert.assertEquals("Salla", conversationRepo.findOne("224r").getMessagesOfConversation()
                .get(0).getSender());
        Assert.assertEquals("Hoi", conversationRepo.findOne("224r")
                .getMessagesOfConversation().get(0).getContent());
        Assert.assertEquals("23.4.2005", conversationRepo.findOne("224r")
                .getMessagesOfConversation().get(0).getDate());
        Assert.assertEquals("224r", conversationRepo.findOne("224r")
                .getMessagesOfConversation().get(0).getConversation().getChannelId());
    }

    @Test
    @Transactional
    public void saveMsgToDatabaseTest3() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("xxx");
        conversationRepo.save(conversation);
        databaseService.saveMsgToDatabase("Salla", "Hoi", "23.4.2005", "224r");
        databaseService.saveMsgToDatabase("Anon", "Moi", "23.5.2005", "224r");
        List<Message> messages = conversationRepo.findOne("224r")
                .getMessagesOfConversation();
        Assert.assertEquals(2, messages.size());
        Assert.assertNotEquals(messages.get(0), messages.get(1));
        Assert.assertEquals(messages.get(1).getConversation(), messages.get(0).getConversation());
        Assert.assertEquals("Moi", messages.get(1).getContent());
    }

}
