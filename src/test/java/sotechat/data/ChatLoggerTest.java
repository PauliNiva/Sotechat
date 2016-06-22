package sotechat.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sotechat.Launcher;
import sotechat.domainService.ConversationService;
import sotechat.domainService.PersonService;
import sotechat.repo.ConversationRepo;
import sotechat.repo.PersonRepo;
import sotechat.service.DatabaseService;
import sotechat.util.MockHttpServletRequest;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Created by varkoi on 22.6.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@Transactional
@ActiveProfiles("development")
public class ChatLoggerTest {

    @Autowired
    PersonRepo personRepo;
    @Autowired
    ConversationRepo conversationRepo;
    @Autowired
    DatabaseService dbservice;

    private ChatLogger chatlogger;
    private SessionRepo srepo;
    private Mapper mapper;

    private MsgToServer message;
    private HttpServletRequest request;
    private Session session;

    @Before
    public void setUp(){
        mapper = new Mapper();
        srepo = new SessionRepo(mapper);
        dbservice.createConversation("Salla", "xxx", "hammashoito");
        dbservice.addPersonToConversation("666", "xxx");
        request = new MockHttpServletRequest("sessionx");
        session = srepo.updateSession(request, null);
        session.set("username", "Salla");
        String userId = session.get("userId");
        message = MsgToServer.create(userId, "xxx", "Moi!");
        chatlogger = new ChatLogger(srepo, dbservice);
    }

    @Test
    public void logNewMessageTest(){
        chatlogger.logNewMessage(message);
        List<MsgToClient> log = chatlogger.getLogs("xxx");
        Assert.assertFalse(log.isEmpty());
        MsgToClient msg = log.get(0);
        Assert.assertEquals("Moi!", msg.getContent());
        Assert.assertEquals("Salla", msg.getUsername());
        Assert.assertEquals("xxx", msg.getChannelId());
        Assert.assertNotNull(msg.getTimeStamp());
        Assert.assertFalse(dbservice.retrieveMessages("xxx").isEmpty());
        List<MsgToClient> dbmsgs = dbservice.retrieveMessages("xxx");
        Assert.assertEquals("Moi!", dbmsgs.get(0).getContent());
        Assert.assertEquals("Salla", dbmsgs.get(0).getUsername());
        Assert.assertEquals("xxx", dbmsgs.get(0).getChannelId());
        Assert.assertNotNull(dbmsgs.get(0).getTimeStamp());
    }

}
