package sotechat.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sotechat.data.Mapper;
import sotechat.data.MapperImpl;
import sotechat.data.SessionRepo;
import sotechat.data.SessionRepoImpl;
import sotechat.data.*;
import sotechat.domain.Conversation;
import sotechat.domainService.ConversationService;
import sotechat.domainService.MessageService;
import sotechat.domainService.PersonService;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;
import sotechat.service.DatabaseService;
import sotechat.service.ValidatorService;
import sotechat.util.MockMockHttpSession;
import sotechat.util.MockPrincipal;
import sotechat.service.QueueService;

import java.util.HashMap;
import java.util.Map;

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
    private SessionRepo sessions;

    /** Before.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        Mapper mapper = new MapperImpl();
        sessions = new SessionRepoImpl(mapper);
        ConversationRepo mockConversationRepo = mock(ConversationRepo.class);
        MessageRepo mockMessageRepo = mock(MessageRepo.class);
        when(mockConversationRepo.findOne(any(String.class)))
                .thenReturn(new Conversation());
        when(mockConversationRepo.getOne(any(String.class)))
                .thenReturn(new Conversation());
        PersonRepo mockPersonRepo = mock(PersonRepo.class);
        ConversationService conversationService = new ConversationService(
                mockConversationRepo, mockPersonRepo);
        PersonService personService = new PersonService(mockPersonRepo);
        MessageService messageService = new MessageService(mockMessageRepo);
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
        DatabaseService databaseService = new DatabaseService(personService,
                                            conversationService,
                                            messageService);
        ChatLogger chatLogger = new ChatLogger(mapper, databaseService);
        QueueService qService = new QueueService(
                mapper, sessions, databaseService, chatLogger
        );
        QueueBroadcaster broadcaster = new QueueBroadcasterImpl(qService, broker);
        ValidatorService validatorService = new ValidatorService(
                mapper, sessions
        );
        SubscribeEventListener listener = new SubscribeEventListener();
//        SubscribeEventListener listener = new SubscribeEventListener(
//                sessions,
//                broadcaster,
//                broker,
//                chatLogger,
//                mapper
//        );
        mvc = MockMvcBuilders
                .standaloneSetup(new StateController(
                        validatorService,
                        sessions,
                        qService,
                        broadcaster))
                .build();
    }

    /** GET polkuun /userState palauttaa uskottavat arvot.
     * @throws Exception
     */
    @Test
    public void testGetUserStateReturnsPlausibleValues() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/userState").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$.state", is("start")))
                .andExpect(jsonPath("$.username", is("Anon")))
                .andExpect(jsonPath("$.channelId").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.category").isNotEmpty());
    }

    @Test
    public void testGetProStateReturnsPlausibleValues() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                    .get("/proState")
                    .accept(MediaType.APPLICATION_JSON)
                        .principal(new MockPrincipal("hoitaja")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.state").isNotEmpty())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.online").isNotEmpty())
                .andExpect(jsonPath("$.qbcc", is("QBCC")))
                .andExpect(jsonPath("$.channelIds", is("[]")));
    }


    @Test
    public void joinPoolWithoutProperSessionFails() throws Exception {
        String json = "{\"username\":\"Markku\",\"startMessage\":\"Hei!\"}";
        mvc.perform(post("/joinPool")
                .contentType(MediaType.APPLICATION_JSON).content(json)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.content",
                        is("Denied due to missing or invalid session ID.")));
    }

    @Test
    public void joinPoolTypicalCaseWorks() throws Exception {
        MockMockHttpSession mockSession = new MockMockHttpSession("007");
        /** Tehdaan aluksi pyynto /userState, jotta saadaan session 007
         * alkutilaksi "start", joka mahdollistaa /joinPool pyynnot. */
        MvcResult result = mvc
                .perform(MockMvcRequestBuilders
                        .get("/userState")
                        .session(mockSession)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        /** Naita ei kayteta. Sailytetaan tulevia testeja varten!
        String response = result.getResponse().getContentAsString();
        JsonObject jsonObj = new JsonParser().parse(response).getAsJsonObject();
        String channelId = jsonObj.get("channelId").toString();
        String userId = jsonObj.get("userId").toString(); */

        /** Tehdaan sitten samalta 007-sessiolta kelpo /joinPool pyynto. */
        String json = "{\"username\":\"Anon\",\"startMessage\":\"Hei!\"}";
        mvc.perform(post("/joinPool")
                    .contentType(MediaType.APPLICATION_JSON).content(json)
                    .session(mockSession)
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.content",
                        is("OK, please request new state now.")));
    }

    @Test
    public void joinPoolWithWrongStateFails() throws Exception {
        MockMockHttpSession mockSession = new MockMockHttpSession("007");
        /** Tehdaan aluksi pyynto /userState, jotta saadaan session 007
         * alkutilaksi "start", joka mahdollistaa /joinPool pyynnot. */
        mvc.perform(MockMvcRequestBuilders
                .get("/userState")
                .session(mockSession)
                .accept(MediaType.APPLICATION_JSON));

        /** Asetetaan palvelimella session tilaksi "chat". */
        sessions.getSessionObj("007").set("state", "chat");

        /** Tehdaan /joinPool pyynto sessiolta 007, vaikka tila eioo "start". */
        String json = "{\"username\":\"Anon\",\"startMessage\":\"Hei!\"}";
        mvc.perform(post("/joinPool")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .session(mockSession)
                        )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.*", hasSize(1)))
                    .andExpect(jsonPath("$.content",
                        is("Denied join pool request due to bad state.")));
    }

    @Test
    public void joinPoolWithReservedScreennameFails() throws Exception {
        MockMockHttpSession mockSession = new MockMockHttpSession("007");
        /** Tehdaan aluksi pyynto /userState, jotta saadaan session 007
         * alkutilaksi "start", joka mahdollistaa /joinPool pyynnot. */
        mvc.perform(MockMvcRequestBuilders
                        .get("/userState")
                        .session(mockSession)
                        .accept(MediaType.APPLICATION_JSON));

        /** Tehdaan sitten samalta 007-sessiolta /joinPool pyynto,
         * jossa yritamme valita rekisteroidyn kayttajanimen "Hoitaja". */
        String json = "{\"username\":\"Hoitaja\",\"startMessage\":\"Hei!\"}";
        mvc.perform(post("/joinPool")
                .contentType(MediaType.APPLICATION_JSON).content(json)
                .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.content",
                        is("Denied join pool request due "
                                + "to reserved username.")));
    }

}