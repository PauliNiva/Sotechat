package sotechat.domainService;

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
import sotechat.repo.ConversationRepo;

import javax.transaction.Transactional;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@Transactional
@ActiveProfiles("development")
public class ConversationServiceTest {

    Conversation conversation;
    Message message1;
    Message message2;

    @Autowired
    ConversationService conversationService;

    @Autowired
    ConversationRepo conversationRepo;

    @Before
    public void setUp() {
        this.conversation = new Conversation();
        this.conversation.setChannelId("007");
        this.conversation.setDate("2006");
        this.message1 = new Message();
        this.message2 = new Message();
        this.message1.setContent("Sisältö");
        this.message1.setSender("Pauli");
        this.message1.setConversation(this.conversation);
        this.message2.setContent("Toinen sisältö");
        this.message2.setSender("Pauli");
        this.message2.setConversation(this.conversation);
    }

    @Test
    public void conversationsAreRemovedFromRepo() throws Exception {
        Assert.assertEquals(0,
                this.conversation.getMessagesOfConversation().size());
        this.conversation.addMessageToConversation(message1);
        this.conversation.addMessageToConversation(message2);
        Assert.assertEquals(2,
                this.conversation.getMessagesOfConversation().size());
        conversationService.addConversation(conversation);
        Assert.assertEquals(1, conversationRepo.count());
        conversationService.deleteConversation("007");
        Assert.assertEquals(0, conversationRepo.count());
    }

    @Test
    public void testi() throws Exception {
        conversationService.addConversation(conversation);
        conversationService.addMessage(message1, conversation);
        Assert.assertEquals(1, conversation.getMessagesOfConversation().size());
        conversationService.addMessage(message2, conversation);
        Conversation conversation2 = conversationService.getConversation("007");
        Assert.assertEquals(2,
                conversation2.getMessagesOfConversation().size());
        conversationService.removeMessage(message1);
        Conversation conversation3 = conversationService.getConversation("007");
        Assert.assertEquals(1,
                conversation3.getMessagesOfConversation().size());
    }
}
