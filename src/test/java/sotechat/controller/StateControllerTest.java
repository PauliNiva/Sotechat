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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sotechat.Launcher;
import sotechat.data.Mapper;
import sotechat.data.SessionRepo;


import sotechat.repo.ConversationRepo;
import sotechat.util.MockMockHttpSession;
import sotechat.util.MockPrincipal;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.*;


/**
 * StateControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@WebAppConfiguration
@Transactional
public class StateControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;
    private SessionRepo sessions;
    private Mapper mapper;

    @Autowired
    private ConversationRepo conversationRepo;

    /** Before.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        mapper = (Mapper)webApplicationContext.getBean("mapper");
        sessions = (SessionRepo)webApplicationContext
                .getBean("sessionRepo");
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
        /* Unohdetaan sessiot, jotta testien valille
         * ei syntyisi riippuvaisuuksia. */
        sessions.forgetSessions();
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
    public void cantAccessProStateWithoutAuthentication() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/proState")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals("", content);
    }

    @Test
    public void testJoinQueueWithoutProperSessionFails() throws Exception {
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
    public void testJoinQueueTypicalCaseWorks() throws Exception {
        MockMockHttpSession mockSession = new MockMockHttpSession("007");
        /** Tehdaan aluksi pyynto /userState, jotta saadaan session 007
         * alkutilaksi "start", joka mahdollistaa /joinQueue pyynnot. */
        MvcResult result = mvc
                .perform(MockMvcRequestBuilders
                        .get("/userState")
                        .session(mockSession)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();


        /* Naita ei kayteta. Sailytetaan tulevia testeja varten!
        String response = result.getResponse().getContentAsString();
        JsonObject jsonObj = new JsonParser().parse(response).getAsJsonObject();
        String channelId = jsonObj.get("channelId").toString();
        String userId = jsonObj.get("userId").toString();*/

        /** Tehdaan sitten samalta 007-sessiolta kelpo /joinQueue pyynto. */
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
    public void testJoinQueueWithWrongStateFails() throws Exception {
        MockMockHttpSession mockSession = new MockMockHttpSession("007");
        /** Tehdaan aluksi pyynto /userState, jotta saadaan session 007
         * alkutilaksi "start", joka mahdollistaa /joinQueue pyynnot. */
        mvc.perform(MockMvcRequestBuilders
                .get("/userState")
                .session(mockSession)
                .accept(MediaType.APPLICATION_JSON));

        /** Asetetaan palvelimella session tilaksi "chat". */
        sessions.getSessionFromSessionId("007").set("state", "chat");

        /** Tehdaan /joinQueue pyynto sessiolta 007, vaikka tila != "start". */
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
    public void testJoinQueueWithReservedScreennameFails() throws Exception {
        MockMockHttpSession mockSession = new MockMockHttpSession("007");
        this.mapper.mapProUsernameToUserId("hoitaja", "666");

        /** Tehdaan aluksi pyynto /userState, jotta saadaan session 007
         * alkutilaksi "start", joka mahdollistaa /joinQueue pyynnot. */
        mvc.perform(MockMvcRequestBuilders
                        .get("/userState")
                        .session(mockSession)
                        .accept(MediaType.APPLICATION_JSON));

        /** Tehdaan sitten samalta 007-sessiolta /joinQueue pyynto,
         * jossa yritamme valita rekisteroidyn kayttajanimen "Hoitaja". */
        String json = "{\"username\":\"hoitaja\",\"startMessage\":\"Hei!\"}";
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
