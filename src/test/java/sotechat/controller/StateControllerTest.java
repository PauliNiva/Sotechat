package sotechat.controller;

import com.google.gson.Gson;
import groovy.json.JsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationListener;
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
import sotechat.queue.Queue;
import sotechat.queue.QueueImpl;
import sotechat.service.ChatMessageService;
import sotechat.service.QueueService;
import sotechat.service.StateService;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.*;


/**
 * StateControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class StateControllerTest {

    /**
     * MockMvc.
     */
    private MockMvc mvc;

    /**
     * Before.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        Mapper mapper = new MapperImpl();
        SubscribeEventListener listener = new SubscribeEventListener();
        QueueService qService = new QueueService(new QueueImpl());
        SessionRepo sessions = new SessionRepoImpl(mapper);
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
        StateService state = new StateService(
                mapper, listener, qService, sessions);
        mvc = MockMvcBuilders
                .standaloneSetup(new StateController(state, broadcaster))
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
                .get("/proState").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProStateReturnsPlausibleValues() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/proState").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.state", is("start")))
                .andExpect(jsonPath("$.username", is("Anon")))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.online", is("true")))
                .andExpect(jsonPath("$.qbcc", is("QBCC")))
                .andExpect(jsonPath("$.channelIds", is("")));
    }

    @Test
    public void
    testJoinPoolSucceedsWithNormalUserAndStartMessage() throws Exception {

        String json = "{\"username\":\"Anon\",\"startMessage\":\"Hei!\"}";
        mvc.perform(MockMvcRequestBuilders.post("/joinPool")
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

}