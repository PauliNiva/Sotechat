package sotechat.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
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
       // SimpMessagingTemplate broker = new SimpMessagingTemplate();
        StateService state = new StateService(
                mapper, listener, qService, sessions);
       // mvc = MockMvcBuilders
        //        .standaloneSetup(new StateController(state, broker))
        //        .build();
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

}