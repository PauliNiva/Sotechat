package sotechat.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sotechat.Launcher;
import sotechat.repo.ConversationRepo;
import sotechat.repo.PersonRepo;
import sotechat.service.DatabaseService;
import sotechat.util.MockHttpServletRequest;
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    private String userId;

    @Before
    public void setUp(){
        mapper = new Mapper();
        srepo = new SessionRepo(mapper);
        request = new MockHttpServletRequest("sessionx");
        session = srepo.updateSession(request, null);
        session.set("username", "Salla");
        userId = session.get("userId");
        message = MsgToServer.create(userId, "xxx", "Moi!");
        dbservice.createConversation("Salla", "xxx", "hammashoito");
        dbservice.addPersonToConversation("666", "xxx");
        chatlogger = new ChatLogger(srepo, dbservice);
    }

    private boolean equals(MsgToClient first, MsgToClient second){
        if(!first.getChannelId().equals(second.getChannelId()))    return false;
        if(!first.getContent().equals(second.getContent())) return false;
        if(!first.getTimeStamp().equals(second.getTimeStamp())) return false;
        if(!first.getUsername().equals(second.getUsername()))   return false;
        return true;
    }

    @Test
    public void logNewMessageTest(){
        MsgToClient saved = chatlogger.logNewMessage(message);
        List<MsgToClient> log = chatlogger.getLogs("xxx");
        Assert.assertFalse(log.isEmpty());
        MsgToClient msg = log.get(0);
        Assert.assertEquals(saved, msg);
        Assert.assertEquals("Moi!", msg.getContent());
        Assert.assertEquals("Salla", msg.getUsername());
        Assert.assertEquals("xxx", msg.getChannelId());
        Assert.assertNotNull(msg.getTimeStamp());
        Assert.assertFalse(dbservice.retrieveMessages("xxx").isEmpty());
        List<MsgToClient> dbmsgs = dbservice.retrieveMessages("xxx");
        Assert.assertTrue(equals(saved, dbmsgs.get(0)));
    }

    @Test
    public void broadcastTest(){
        SimpMessagingTemplate mockBroker = Mockito.mock(SimpMessagingTemplate.class);
        MsgToClient m1 = chatlogger.logNewMessage(message);
        MsgToClient m2 = chatlogger.logNewMessage(MsgToServer.create(userId, "xxx", "haloo"));
        chatlogger.broadcast("xxx", mockBroker);
        verify(mockBroker, times(1)).convertAndSend("/toClient/chat/xxx", m1);
        verify(mockBroker, times(1)).convertAndSend("/toClient/chat/xxx", m2);
    }

    @Test
    public void getChannelsByUserIdTest(){
        dbservice.addPersonToConversation("666", "xxx");
        dbservice.saveMsg("Salla", "Haloo", "20.10.", "xxx");
        List<ConvInfo> channelinfo = chatlogger.getChannelsByUserId("666");
        Assert.assertFalse(channelinfo.isEmpty());
        ConvInfo info = channelinfo.get(0);
        Assert.assertEquals("xxx", info.getChannelId());
        Assert.assertEquals("Salla", info.getPerson());
        Assert.assertNotNull(info.getDate());
        Assert.assertEquals("hammashoito", info.getCategory());
    }

    @Test
    public void getLogsTest(){
        List<MsgToClient> logs = chatlogger.getLogs("xxx");
        Assert.assertTrue(logs.isEmpty());
        MsgToClient saved = chatlogger.logNewMessage(message);
        logs = chatlogger.getLogs("xxx");
        Assert.assertFalse(logs.isEmpty());
        Assert.assertEquals(saved, logs.get(0));
    }

    @Test
    public void removeOldMessagesFromMemoryTest(){
        //ToDo
    }

}
