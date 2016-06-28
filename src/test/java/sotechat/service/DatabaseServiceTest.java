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
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;

import javax.transaction.Transactional;
import java.util.List;

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
    @Transactional
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
        databaseService.saveMsg("Salla", "Hoi", "23.4.2005", "224r");
        Assert.assertNotNull(conversationRepo.findOne("224r"));
        Assert.assertNotNull(conversationRepo.findOne("224r").getMessagesOfConversation().get(0));
    }

    @Test
    @Transactional
    public void saveMsgToDatabaseTest2() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("xxx");
        conversationRepo.save(conversation);
        databaseService.saveMsg("Salla", "Hoi", "23.4.2005", "224r");
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
        databaseService.saveMsg("Salla", "Hoi", "23.4.2005", "224r");
        databaseService.saveMsg("Anon", "Moi", "23.5.2005", "224r");
        List<Message> messages = conversationRepo.findOne("224r")
                .getMessagesOfConversation();
        Assert.assertEquals(2, messages.size());
        Assert.assertNotEquals(messages.get(0), messages.get(1));
        Assert.assertEquals(messages.get(1).getConversation(), messages.get(0).getConversation());
        Assert.assertEquals("Moi", messages.get(1).getContent());
    }

    @Test
    @Transactional
    public void personsConversationsTest() throws Exception {
        Assert.assertTrue(databaseService.getConvInfoListOfUserId("xxd").isEmpty());
        Assert.assertEquals(0, conversationRepo.count());
        conversationRepo.save(new Conversation("22xx", "1"));

        Assert.assertEquals(1, conversationRepo.count());
        databaseService.addPersonToConversation("xxd", "22xx");
        Assert.assertFalse(databaseService.getConvInfoListOfUserId("xxd").isEmpty());
    }

    @Test
    @Transactional
    public void personsConversationsTest2() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("2");
        conversationRepo.save(conversation);
        Conversation c2 = new Conversation("333f", "1" );
        conversationRepo.save(c2);
        databaseService.addPersonToConversation("xxd", "224r");
        databaseService.addPersonToConversation("xxd", "333f");
        List<ConvInfo> channelIds = databaseService.getConvInfoListOfUserId("xxd");
        Assert.assertEquals(2, channelIds.size());
        Assert.assertEquals("224r", channelIds.get(0).getChannelId());
        Assert.assertEquals("333f", channelIds.get(1).getChannelId());
    }

    @Test
    @Transactional
    public void personsConversationsTest3() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("2");
        conversationRepo.save(conversation);
        databaseService.addPersonToConversation("xxd", "224r");
        databaseService.saveMsg("Anon", "moi", "1", "224r");
        List<ConvInfo> convInfo = databaseService.getConvInfoListOfUserId("xxd");
        Assert.assertEquals(1, convInfo.size());
        Assert.assertEquals("224r", convInfo.get(0).getChannelId());
        Assert.assertEquals("Anon", convInfo.get(0).getPerson());
    }

    @Test
    @Transactional
    public void retrieveMessagesTest() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("2");
        conversationRepo.save(conversation);
        databaseService.saveMsg("Salla", "Moi", "2", "224r");
        databaseService.saveMsg("Anon", "Moikka!", "1", "224r");
        List<MsgToClient> msgs = databaseService.retrieveMessages("224r");
        Assert.assertEquals(2, msgs.size());
        Assert.assertEquals("Moi", msgs.get(0).getContent());
        Assert.assertEquals("Moikka!", msgs.get(1).getContent());
    }

    @Test
    @Transactional
    public void retrieveMessagesTest2() throws Exception {
        conversation.setChannelId("224r");
        conversation.setDate("2");
        conversationRepo.save(conversation);
        databaseService.saveMsg("Salla", "Moi", "2", "224r");
        List<MsgToClient> msgs = databaseService.retrieveMessages("224r");
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
        conversationRepo.save(conversation);
        conversationRepo.save(new Conversation("1xxx","3"));
        databaseService.saveMsg("Salla", "Moi", "2", "224r");
        databaseService.saveMsg("Anon", "Hello", "2", "1xxx");
        List<MsgToClient> msgs = databaseService.retrieveMessages("224r");
        List<MsgToClient> msgs2 = databaseService.retrieveMessages("1xxx");
        Assert.assertEquals(1, msgs.size());
        Assert.assertEquals(1, msgs2.size());
        Assert.assertNotEquals(msgs.get(0).getContent(), msgs2.get(0).getContent());
    }

    @Test
    public void invalidChannelIdRetvieveMessages() {
        Assert.assertEquals(0,
                databaseService.retrieveMessages("tataEiloydy").size());
    }

    @Test
    public void invalidUserlIdConvInfo() {
        Assert.assertEquals(0,
                databaseService.getConvInfoListOfUserId("tataEiloydy").size());
    }

    @Test
    @Transactional
    public void invalidDataCreateConversation() {
        databaseService.createConversation(null,null, null);
        Assert.assertEquals(0, conversationRepo.findAll().size());
    }

    @Test
    @Transactional
    public void invalidDataSaveMessage() {
        databaseService.saveMsg("Anon",null, null, "1xxx");
        Assert.assertEquals(0, databaseService.retrieveMessages("1xxx").size());
    }

    @Test
    @Transactional
    public void invalidDataAddPerson() {
        databaseService.addPersonToConversation("xxd", "tataEiloydy");
        Assert.assertEquals(0, databaseService.getConvInfoListOfUserId("xxd").size());
    }


}
