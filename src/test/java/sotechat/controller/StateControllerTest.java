package sotechat.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sotechat.data.Mapper;
import sotechat.data.MapperImpl;
import sotechat.data.SessionRepo;
import sotechat.data.SessionRepoImpl;
import sotechat.data.*;
import sotechat.data.QueueImpl;
import sotechat.domain.Conversation;
import sotechat.domainService.ConversationService;
import sotechat.repo.ConversationRepo;
import sotechat.repo.PersonRepo;
import sotechat.util.MockPrincipal;
import sotechat.websocketService.QueueService;
import sotechat.websocketService.StateService;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.*;


/**
 * StateControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class StateControllerTest {

    private MockMvc mvc;

    /**
     * Before.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        ChatLogger chatLogger = new ChatLogger();
        Mapper mapper = new MapperImpl();
        SubscribeEventListener listener = new SubscribeEventListener();
        QueueService qService = new QueueService(new QueueImpl());
        SessionRepo sessions = new SessionRepoImpl(mapper);
        ConversationRepo mockConversationRepo = mock(ConversationRepo.class);
        when(mockConversationRepo.findOne(any(String.class)))
                .thenReturn(new Conversation());
        PersonRepo mockPersonRepo = mock(PersonRepo.class);
        ConversationService conversationService = new ConversationService(
                mockConversationRepo, mockPersonRepo);
        SimpMessagingTemplate broker = new SimpMessagingTemplate(
                new MessageChannel() {
            @Override
            public boolean send(Message<?> message) {
                return true;
            }

            @Override
            public boolean send(Message<?> message, long l) {
                return true;
            }
        });
        QueueBroadcaster broadcaster = new QueueBroadcaster(qService, broker);
        ChatLogBroadcaster logBroadcaster = new ChatLogBroadcaster(
                chatLogger, broker);
        StateService state = new StateService(
                mapper, listener, qService, chatLogger, sessions, conversationService);
        mvc = MockMvcBuilders
                .standaloneSetup(new StateController(
                        state, broadcaster, logBroadcaster, conversationService))
                .build();
    }

    /** Get pyynto polkuun "/userState" palauttaa statukseksen OK.
     * @throws Exception
     */
    @Test
    public void testGetUserStateReturnsOK() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/userState").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /** GET polkuun /userState palauttaa uskottavat arvot.
     * @throws Exception
     */
    @Test
    public void testGetUserStateReturnsPlausibleValues() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/userState").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$.state", is("start")))
                .andExpect(jsonPath("$.username", is("Anon")))
                .andExpect(jsonPath("$.channelId").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.category").isNotEmpty());
    }

    @Test
    public void testGetProStatReturnsOK() throws Exception {
         mvc.perform(MockMvcRequestBuilders
                .get("/proState").accept(MediaType.APPLICATION_JSON)
                    .principal(new MockPrincipal("hoitaja")))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProStateReturnsPlausibleValues() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                    .get("/proState")
                    .accept(MediaType.APPLICATION_JSON)
                        .principal(new MockPrincipal("hoitaja")))
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.state").isNotEmpty())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.online").isNotEmpty())
                .andExpect(jsonPath("$.qbcc", is("QBCC")))
                .andExpect(jsonPath("$.channelIds", is("[]")));
    }

    @Test
    public void
    joiningChatPoolSucceedsWithNormalUserIfStateIsStart() throws Exception {
        String json = "{\"username\":\"Anon\",\"startMessage\":\"Hei!\"}";
        mvc.perform(post("/joinPool")
                    .contentType(MediaType.APPLICATION_JSON).content(json)
                    .sessionAttr("channelId", "2")
                    .sessionAttr("state", "start")
                    .sessionAttr("userId", "4")
                    .sessionAttr("category", "DRUGS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.content",
                        is("OK, please request new state now.")));
    }

    @Test
    public void
    joiningChatPoolFailsWithNormalUserIfStateIsNotStart() throws Exception {
        String json = "{\"username\":\"Anon\",\"startMessage\":\"Hei!\"}";
        mvc.perform(post("/joinPool")
                    .contentType(MediaType.APPLICATION_JSON).content(json)
                    .sessionAttr("channelId", "2")
                    .sessionAttr("state", "chat")
                    .sessionAttr("userId", "4")
                    .sessionAttr("category", "DRUGS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.content",
                        is("Denied join pool request due to bad state.")));
    }

    @Test
    public void
    joiningChatPoolFailsIfUserTriesToJoinWithProfessionalIdAndUsername() throws Exception {
        String json = "{\"username\":\"Hoitaja\",\"startMessage\":\"Hei!\"}";
        mvc.perform(post("/joinPool")
                .contentType(MediaType.APPLICATION_JSON).content(json)
                .sessionAttr("channelId", "2")
                .sessionAttr("state", "start")
                .sessionAttr("userId", "666")
                .sessionAttr("category", "DRUGS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.content",
                    is("Denied join pool request due to reserved username.")));
    }

}