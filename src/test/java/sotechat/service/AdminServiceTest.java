package sotechat.service;


import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Base64Utils;
import sotechat.Launcher;
import sotechat.data.*;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;
import sotechat.util.MockHttpServletRequest;
import sotechat.wrappers.MsgToServer;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;

import static org.springframework.security.crypto.bcrypt.BCrypt.hashpw;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@Transactional
@ActiveProfiles("development")
public class AdminServiceTest {

    private Person person;
    private String jsonPerson;
    private Gson gson;
    private String encodedPerson;

    private HttpServletRequest request;
    private Session session;

    @Autowired
    AdminService adminService;

    @Autowired
    PersonRepo personRepo;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private Mapper mapper;

    @Autowired
    private ChatLogger chatLogger;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ConversationRepo convRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Before
    public void setUp() {
//        person = personRepo.findOne("admin");
        person = new Person("opqr");
        person.setLoginName("salla");
        person.setUserName("Salla");
        person.hashPasswordWithSalt("salasana");
        gson = new Gson();
        jsonPerson = gson.toJson(person);
        encodedPerson = Base64Utils.encodeToString(jsonPerson.getBytes());
    }

    @Test
    @Transactional
    public void addUserTest() throws Exception {
        Assert.assertEquals(2, personRepo.count());
        String reply = adminService.addUser(encodedPerson);
        Assert.assertEquals("", reply);
        Assert.assertEquals(3, personRepo.count());
        Assert.assertNotNull(personRepo.findByLoginName("salla")); // ToDo: korjaa personRepo.findOne() (miksi ei toimi?)
    }

    @Test
    @Transactional
    public void listAllPersonsAsJsonListTest(){
        String json = adminService.listAllPersonsAsJsonList();
        ArrayList<Person> list = gson.fromJson(json, ArrayList.class);
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
    }

    @Test
    @Transactional
    public void deleteUserTest(){
        Assert.assertNotNull(personRepo.findOne("666"));
        adminService.deleteUser("666");
        Assert.assertNull(personRepo.findOne("666"));
        Assert.assertFalse(mapper.isUsernameReserved("Hoitaja"));
    }

    @Test
    public void makePersonFromTest(){
        Person person = adminService.makePersonFrom(encodedPerson);
        Assert.assertEquals("ROLE_USER", person.getRole());
        Assert.assertEquals("Salla", person.getUserName());
        Assert.assertEquals("salla", person.getLoginName());
        Assert.assertEquals("opqr", person.getUserId());
    }

    @Test
    public void changePassWordTest(){
        adminService.changePassword("666", Base64Utils.encodeToString("uusisalasana".getBytes()));
        Person person = personRepo.findOne("666");
        String hashed = person.getHashOfPasswordAndSalt();
        String salt = person.getSalt();
        String hsh2 = BCrypt.hashpw("uusisalasana", salt);
        Assert.assertEquals(hsh2, hashed);
    }

    @Test
    public void clearhistoryTest(){
        request = new MockHttpServletRequest("sessionx");
        request.setAttribute("username", "Salla");
        session = sessionRepo.updateSession(request, null);
        Channel channel = mapper.createChannel();
        String channelId = channel.getId();
        databaseService.createConversation("Salla", channelId, "hammaslaakari");
        session.set("username", "Salla");
        session.set("channelId", channelId);
        session.set("userId", "x");
        session.set("category", "hmm");
        sessionRepo.updateSessionAttributes(session, null);
        queueService.joinQueue(session, "Salla", "Moikkis");
        MsgToServer msg = MsgToServer.create("666", channelId, "haloo");
        sessionRepo.getSessionsByUserId().put("666", session);
        chatLogger.logNewMessage(msg);
        adminService.clearHistory();
        Assert.assertTrue(sessionRepo.getSessionsByUserId().isEmpty());
        Assert.assertTrue(sessionRepo.getSessionsBySessionId().isEmpty());
        Assert.assertTrue(sessionRepo.getProUserSessions().isEmpty());
        Assert.assertTrue(chatLogger.getLogs(channelId).isEmpty());
        Assert.assertEquals(0, queueService.getQueueLength());
        Assert.assertEquals(0, convRepo.count());
        Assert.assertEquals(0, messageRepo.count());
    }

}
