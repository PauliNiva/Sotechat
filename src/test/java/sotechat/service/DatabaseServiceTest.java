package sotechat.service;

import org.apache.poi.ss.formula.functions.T;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sotechat.Application;
import sotechat.Launcher;
import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.domainService.ConversationService;
import sotechat.domainService.MessageService;
import sotechat.domainService.PersonService;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;
import sotechat.wrappers.MsgToClient;

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
    PersonRepo pr;

    @Autowired
    ConversationRepo cr;

    @Autowired
    MessageRepo mr;

    @Autowired
    PersonService pservice;

    @Autowired
    ConversationService cservice;

    @Autowired
    MessageService mservice;

    @Autowired
    DatabaseService dbservice;

    @Before
    public void setUp() throws Exception {
        person = new Person("xxd");
        pr.save(person);
        conversation = new Conversation();
        conversation.setChannelId("xyzo");
    }

    @Test
    @Transactional
    public void createConversationTest() throws Exception {
        dbservice.createConversation("Anon", "Moi!", "888a", "hammashoito");
        Assert.assertEquals("Conversation", cr.findAll().get(0).getClass().getSimpleName());
        Assert.assertNotNull(cr.findOne("888a"));
        Assert.assertNotNull(cr.findOne("888a").getDate());
        Assert.assertEquals("Moi!", cr.findOne("888a").getMessagesOfConversation().get(0).getContent());
        Assert.assertEquals("Anon", cr.findOne("888a").getMessagesOfConversation().get(0).getSender());
        Assert.assertEquals("888a", cr.findOne("888a").getMessagesOfConversation().get(0).getChannelId());
        Assert.assertNotNull("Anon", cr.findOne("888a").getMessagesOfConversation().get(0).getDate());
    }

    @Test
    @Transactional
    public void createConversationTest2() throws Exception {
        dbservice.createConversation("Anon", "Moi!", "888b", "hammashoito");
        Assert.assertEquals("hammashoito", cr.findOne("888b").getCategory());
    }

    @Test
    @Transactional
    public void createConversationTest3() throws Exception {
        dbservice.createConversation("Anon", "Moi!", "888c", "hammashoito");
        Assert.assertEquals("Message", mr.findAll().get(0).getClass().getSimpleName());
        Assert.assertEquals("Moi!", mr.findByChannelId("888c").get(0).getContent());
        Assert.assertEquals("Anon", mr.findByChannelId("888c").get(0).getSender());
        Assert.assertEquals("888c", mr.findByChannelId("888c").get(0).getChannelId());
        Assert.assertNotNull(mr.findByChannelId("888c").get(0).getDate());
    }

    @Test
    @Transactional
    public void createConversationTest4() throws Exception {
        dbservice.createConversation("Anon", "Moi!", "888c", "hammashoito");
        Assert.assertEquals(mr.findByChannelId("888c").get(0).getDate(), cr.findOne("888c").getDate());
    }

    @Test
    @Transactional
    public void addPersonToConversationTest() throws Exception {
        cr.save(conversation);
        dbservice.addPersonToConversation("xxd", "xyzo");
        Assert.assertEquals("xyzo", pr.findOne("xxd").getConversationsOfPerson().get(0).getChannelId());
   }

    @Test
    @Transactional
    public void addPersonToConversationTest2() throws Exception {
        cr.save(conversation);
        dbservice.addPersonToConversation("xxd", "xyzo");
        Assert.assertEquals("xxd", cr.findOne("xyzo").getParticipantsOfConversation().get(0).getUserId());
    }

    @Test
    @Transactional
    public void saveMsgToDatabaseTest() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("xxx");
        cr.save(conversation);
        dbservice.saveMsgToDatabase("Salla", "Hoi", "23.4.2005", "224r");
        Assert.assertNotNull(mr.findByChannelId("224r"));
        Assert.assertNotNull(cr.findOne("224r").getMessagesOfConversation().get(0));
    }

    @Test
    @Transactional
    public void saveMsgToDatabaseTest2() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("xxx");
        cr.save(conversation);
        dbservice.saveMsgToDatabase("Salla", "Hoi", "23.4.2005", "224r");
        Assert.assertEquals("Salla", mr.findByChannelId("224r").get(0).getSender());
        Assert.assertEquals("Hoi", mr.findByChannelId("224r").get(0).getContent());
        Assert.assertEquals("23.4.2005", mr.findByChannelId("224r").get(0).getDate());
        Assert.assertEquals("224r", mr.findByChannelId("224r").get(0).getConversation().getChannelId());
        Assert.assertEquals(cr.findOne("224r").getMessagesOfConversation().get(0), mr.findByChannelId("224r").get(0).getConversation().getMessagesOfConversation().get(0));
    }

    @Test
    @Transactional
    public void saveMsgToDatabaseTest3() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("xxx");
        cr.save(conversation);
        dbservice.saveMsgToDatabase("Salla", "Hoi", "23.4.2005", "224r");
        dbservice.saveMsgToDatabase("Anon", "Moi", "23.5.2005", "224r");
        List<Message> messages = mr.findByChannelId("224r");
        Assert.assertEquals(2, messages.size());
        Assert.assertNotEquals(messages.get(0), messages.get(1));
        Assert.assertEquals(messages.get(1).getConversation(), messages.get(0).getConversation());
        Assert.assertEquals("Moi", messages.get(1).getContent());
    }

    @Test
    @Transactional
    public void personsConversationsTest() throws Exception {
        Assert.assertTrue(dbservice.personsConversations("xxd").isEmpty());
        cr.save(new Conversation("1", "22xx"));
        dbservice.addPersonToConversation("xxd", "22xx");
        Assert.assertFalse(dbservice.personsConversations("xxd").isEmpty());
    }

    @Test
    @Transactional
    public void personsConversationsTest2() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("2");
        cr.save(conversation);
        Conversation c2 = new Conversation("1", "333f");
        cr.save(c2);
        dbservice.addPersonToConversation("xxd", "224r");
        dbservice.addPersonToConversation("xxd", "333f");
        List<String> channelIds = dbservice.personsConversations("xxd");
        Assert.assertEquals(2, channelIds.size());
        Assert.assertEquals("224r", channelIds.get(0));
        Assert.assertEquals("333f", channelIds.get(1));
    }

    @Test
    @Transactional
    public void retrieveMessagesTest() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("2");
        cr.save(conversation);
        dbservice.saveMsgToDatabase("Salla", "Moi", "2", "224r");
        dbservice.saveMsgToDatabase("Anon", "Moikka!", "1", "224r");
        List<MsgToClient> msgs = dbservice.retrieveMessages("224r");
        Assert.assertEquals(2, msgs.size());
        Assert.assertEquals("Moi", msgs.get(1).getContent());
        Assert.assertEquals("Moikka!", msgs.get(0).getContent());
    }

    @Test
    @Transactional
    public void retrieveMessagesTest2() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("2");
        cr.save(conversation);
        dbservice.saveMsgToDatabase("Salla", "Moi", "2", "224r");
        List<MsgToClient> msgs = dbservice.retrieveMessages("224r");
        Assert.assertEquals(1, msgs.size());
        MsgToClient msg = msgs.get(0);
        Assert.assertEquals("Moi", msg.getContent());
        Assert.assertEquals("Salla", msg.getUsername());
        Assert.assertEquals("2", msg.getTimeStamp());
        Assert.assertEquals("224r", msg.getChannelId());
    }

    @Test
    @Transactional
    public void retrieveMessagesTest3() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("2");
        cr.save(conversation);
        cr.save(new Conversation("3", "1xxx"));
        dbservice.saveMsgToDatabase("Salla", "Moi", "2", "224r");
        dbservice.saveMsgToDatabase("Anon", "Hello", "2", "1xxx");
        List<MsgToClient> msgs = dbservice.retrieveMessages("224r");
        List<MsgToClient> msgs2 = dbservice.retrieveMessages("1xxx");
        Assert.assertEquals(1, msgs.size());
        Assert.assertEquals(1, msgs2.size());
        Assert.assertNotEquals(msgs.get(0).getContent(), msgs2.get(0).getContent());
    }

}
