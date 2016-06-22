package sotechat.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sotechat.Launcher;
import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.domain.Conversation;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;
import sotechat.service.DatabaseService;
import sotechat.util.MockHttpServletRequest;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.*;

/**
 * Created by varkoi on 22.6.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@WebAppConfiguration
@Transactional
public class HistoryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;
    private SessionRepo sessions;
    private Mapper mapper;
    private ChatLogger logger;
    private DatabaseService dbservice;

    private MsgToServer message;
    private Conversation conv;
    private HttpServletRequest reqSalla;
    Session sessionSalla;

    @Autowired
    private ConversationRepo conversationRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private PersonRepo personRepo;

    @Before
    public void setUp() throws Exception {
        mapper = (Mapper)webApplicationContext.getBean("mapper");
        sessions = (SessionRepo)webApplicationContext.getBean("sessionRepo");
        logger = (ChatLogger)webApplicationContext.getBean("chatLogger");
        dbservice = (DatabaseService)webApplicationContext.getBean("databaseService");
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        dbservice.createConversation("Salla", "xxx", "hammashoito");
        dbservice.addPersonToConversation("666", "xxx");
        reqSalla = new MockHttpServletRequest("sessionSalla");
        sessionSalla = sessions.updateSession(reqSalla, null);
        sessionSalla.set("username", "Salla");
        String userId = sessionSalla.get("userId");
        message = MsgToServer.create(userId, "xxx", "Moi!");
        logger.logNewMessage(message);
    }

    @After
    public void tearDown() throws Exception {
        sessions.forgetSessions();
    }

    @Test
    public void getMessagesTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/getLogs/xxx").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].messageId").isNotEmpty())
                .andExpect(jsonPath("$[0].username", is("Salla")))
                .andExpect(jsonPath("$[0].channelId", is("xxx")))
                .andExpect(jsonPath("$[0].timeStamp").isNotEmpty())
                .andExpect(jsonPath("$[0].content", is("Moi!")));
    }
}
