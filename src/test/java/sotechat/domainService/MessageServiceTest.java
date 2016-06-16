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
import sotechat.domain.Message;
import sotechat.repo.MessageRepo;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@Transactional
@ActiveProfiles("development")
public class MessageServiceTest {

    Message message;
    Long messageId;

    @Autowired
    MessageRepo messageRepo;

    @Autowired
    MessageService messageService;

    @Before
    public void setUp() {
        this.message = new Message();
        this.message.setContent("Sisältö");
        this.message.setSender("Pauli");
    }

    @Test
    public void MessageIsSavedAndGetFromRepo() throws Exception {
        Message message2 = messageService.addMessage(this.message);
        messageId = message2.getId();
        Message message3 = messageService.getMessage(messageId);
        Assert.assertEquals("Pauli", message3.getSender());
    }

    @Test
    public void messagesAreRemovedFromRepotest() throws Exception {
        Message message2 = messageService.addMessage(this.message);
        Assert.assertEquals(1, messageRepo.count());
        messageId = message2.getId();
        messageService.removeMessage(messageId);
        Assert.assertEquals(0, messageRepo.count());
    }

}
