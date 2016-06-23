package sotechat.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import sotechat.data.Channel;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.domain.Conversation;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;
import sotechat.service.QueueService;
import sotechat.util.*;
import sotechat.wrappers.QueueItem;


import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

/**
 * Testit chattiin kirjoitettujen viestien kasittelyyn ja kuljetukseen.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        StateControllerQueueTest.TestWebSocketConfig.class,
        StateControllerQueueTest.TestConfig.class,
        StateControllerQueueTest.TestRepoInitConfig.class
})
public class StateControllerQueueTest {

    private Mapper mapper;

    private SessionRepo sessionRepo;

    private SimpMessageHeaderAccessor accessor;

    private List<Session> queueItems;

    @Autowired
    private ConversationRepo conversationRepo;

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private QueueService queueService;

    @Autowired
    private StateController stateController;

    @Before
    public void setUp() throws Exception {
        Mockito.when(personRepo.findOne(any(String.class)))
                .thenReturn(new Person());
        Mockito.when(conversationRepo.findOne(any(String.class)))
                .thenReturn(new Conversation());
        this.accessor = Mockito.mock(SimpMessageHeaderAccessor.class);
        this.stateController = (StateController) context.getBean("stateController");
        this.mapper = (Mapper) context.getBean("mapper");
        this.queueService = (QueueService) context.getBean("queueService");

        this.mapper.mapProUsernameToUserId("hoitaja", "666");
        this.mapper.mapProUsernameToUserId("hoitaja2", "667");
        this.sessionRepo = (SessionRepo) context.getBean("sessionRepo");
        this.queueItems = new ArrayList();
    }

    @After
    public void tearDown() throws Exception {
        /* Unohdetaan sessiot, jotta testien valille
         * ei syntyisi riippuvaisuuksia. */
        sessionRepo.forgetSessions();
        emptyQueue();
    }

    @Test
    public void normalUserCantPopFromQueue() throws Exception {
        Session userInQueue = joinQueue("1111", "Hammas");

        String channelId = userInQueue.get("channelId");

        assertEquals(1, this.queueService.getQueueLength());
        assertEquals("queue", userInQueue.get("state"));

        Mockito.when(accessor.getUser()).thenReturn(null);

        assertEquals("", this.stateController
                .popClientFromQueue(channelId, this.accessor));
    }

    @Test
    public void professionalCanPopFromQueue() throws Exception {
        // Liitytään jonoon sessionId:llä 1111.
        Session userInQueue = joinQueue("1111", "Hammas");

        String channelId = userInQueue.get("channelId");

        assertEquals(1, this.queueService.getQueueLength());
        assertEquals("queue", userInQueue.get("state"));

        Session proSession = logInAsAProfessional("hoitaja");

        // Subscribetaan kirjautuva hoitaja kanavalle
        subscribeSessionToChannel(proSession, channelId);

        assertEquals("pro", proSession.get("state"));

        Mockito.when(this.accessor.getUser()).thenReturn(new MockPrincipal("hoitaja"));
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("SPRING.SESSION.ID", "1234");
        Mockito.when(this.accessor.getSessionAttributes()).thenReturn(sessionAttributes);

        JsonObject response = parseStringIntoJsonObject(this.stateController
                .popClientFromQueue(channelId, this.accessor));

        assertEquals("hoitaja", response.get("channelAssignedTo").getAsString());
        assertEquals(0, this.queueService.getQueueLength());
    }

    @Test(expected = NullPointerException.class)
    public void professionalCantPopFromEmptyQueue() throws Exception {
        Mockito.when(this.accessor.getUser()).thenReturn(new MockPrincipal("hoitaja"));

        this.stateController
                .popClientFromQueue("DEV_CHANNEL", this.accessor);
    }

    @Test
    public void twoProsCantPopSameUser() throws Exception {
        professionalCanPopFromQueue();

        Session session = this.sessionRepo.getSessionFromSessionId("1111");
        String channelId = session.get("channelId");

        Mockito.when(this.accessor.getUser()).thenReturn(new MockPrincipal("hoitaja2"));

        JsonObject response = parseStringIntoJsonObject(this.stateController
                .popClientFromQueue(channelId, this.accessor));

        assertEquals("hoitaja", response.get("channelAssignedTo").getAsString());
    }

    @Test
    public void stateOfPoppedUserChangesToChat() throws Exception {
        professionalCanPopFromQueue();

        Session proSession = this.sessionRepo.getSessionFromSessionId("1234");
        String proState = proSession.get("state");

        Session userSession = this.sessionRepo.getSessionFromSessionId("1111");

        String userState = userSession.get("state");

        assertEquals("pro", proState);
        assertEquals("chat", userState);
    }

    @Test
    public void professionalCanPopMultipleUsersFromQueue() throws Exception {
        // Liitytään jonoon sessionId:llä 1111.
        Session firstUserInQueue = joinQueue("1111", "Hammas");
        String channelIdOfFirstUser = firstUserInQueue.get("channelId");
        assertEquals(1, this.queueService.getQueueLength());

        Session secondUserInQueue = joinQueue("1112", "Hammas");
        String channelIdOfSecondUser = secondUserInQueue.get("channelId");
        assertEquals(2, this.queueService.getQueueLength());

        Session thirdUserInQueue = joinQueue("1113", "Hammas");
        String channelIdOfThirdUser = thirdUserInQueue.get("channelId");
        assertEquals(3, this.queueService.getQueueLength());

        assertEquals("queue", firstUserInQueue.get("state"));
        assertEquals("queue", secondUserInQueue.get("state"));
        assertEquals("queue", thirdUserInQueue.get("state"));

        Session proSession = logInAsAProfessional("hoitaja");

        subscribeSessionToChannel(proSession, channelIdOfFirstUser);
        subscribeSessionToChannel(proSession, channelIdOfSecondUser);
        subscribeSessionToChannel(proSession, channelIdOfThirdUser);

        assertEquals("pro", proSession.get("state"));

        Mockito.when(this.accessor.getUser()).thenReturn(new MockPrincipal("hoitaja"));
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("SPRING.SESSION.ID", "1234");
        Mockito.when(this.accessor.getSessionAttributes()).thenReturn(sessionAttributes);

        JsonObject response = parseStringIntoJsonObject(this.stateController
                .popClientFromQueue(channelIdOfFirstUser, this.accessor));

        assertEquals("hoitaja", response.get("channelAssignedTo").getAsString());
        assertEquals(2, this.queueService.getQueueLength());

        JsonObject response2 = parseStringIntoJsonObject(this.stateController
                .popClientFromQueue(channelIdOfSecondUser, this.accessor));

        assertEquals("hoitaja", response.get("channelAssignedTo").getAsString());
        assertEquals(1, this.queueService.getQueueLength());

        JsonObject response3 = parseStringIntoJsonObject(this.stateController
                .popClientFromQueue(channelIdOfThirdUser, this.accessor));

        assertEquals("hoitaja", response.get("channelAssignedTo").getAsString());
        assertEquals(0, this.queueService.getQueueLength());
    }

    @Test
    public void cantRemoveFromQueueWithNonexistentChannelId() throws Exception {
        joinQueue("1111", "Hammas");
        joinQueue("1112", "Hammas");
        joinQueue("1113", "Hammas");
        this.queueService.removeFromQueue("abc");
        assertEquals(3, this.queueService.getQueueLength());
    }

    @Test
    public void queueServiceFindsTheCorrectNumberOfQueueItemsWithSameCategory()
        throws Exception {
        Session firstUser = joinQueue("1111", "Hammas");
        int length1 = this.queueService.getPositionInQueue(firstUser.get("channelId"), "Hammas");
        assertEquals(1, length1);

        Session secondUser = joinQueue("1112", "Hammas");
        int length2 = this.queueService.getPositionInQueue(secondUser.get("channelId"), "Hammas");
        assertEquals(2, length2);

        Session thirdUser = joinQueue("1113", "Hammas");
        int length3 = this.queueService.getPositionInQueue(thirdUser.get("channelId"), "Hammas");
        assertEquals(3, length3);
    }

    @Test
    public void queueServiceFindsTheCorrectNumberOfQueueItemsWithDifferentCategory()
        throws Exception {
        Session firstUser = joinQueue("1111", "Hammas");
        int length1 = this.queueService.getPositionInQueue(firstUser.get("channelId"), "Hammas");
        assertEquals(1, length1);

        Session secondUser = joinQueue("1112", "Päihteet");
        int length2 = this.queueService.getPositionInQueue(secondUser.get("channelId"), "Päihteet");
        assertEquals(1, length2);

        Session thirdUser = joinQueue("1113", "Hammas");
        thirdUser.set("category", "Hammas");
        int length3 = this.queueService.getPositionInQueue(thirdUser.get("channelId"), "Hammas");
        assertEquals(2, length3);
    }

    @Test
    public void queueServiceDoesntFindNonexistentQueueItem() throws Exception {
        Session firstUser = joinQueue("1111", "Hammas");
        int length1 = this.queueService.getPositionInQueue("randomChannelId", "Hammas");
        assertEquals(-1, length1);
    }

    @Test
    public void professionalCanLeaveChat() throws Exception {
        Session userInQueue = joinQueue("1111", "Hammas");

        String channelId = userInQueue.get("channelId");
        String userId = userInQueue.get("userId");

        assertEquals(1, this.queueService.getQueueLength());
        assertEquals("queue", userInQueue.get("state"));

        HttpServletRequest mockRequest = new MockHttpServletRequest("1234");
        Principal mockPrincipal = new MockPrincipal("hoitaja");
        Session proSession = sessionRepo.updateSession(mockRequest, mockPrincipal);

        // Subscribetaan kirjautuva hoitaja kanavalle
        subscribeSessionToChannel(proSession, channelId);

        assertEquals("pro", proSession.get("state"));

        Mockito.when(this.accessor.getUser()).thenReturn(new MockPrincipal("hoitaja"));
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("SPRING.SESSION.ID", "1234");
        Mockito.when(this.accessor.getSessionAttributes()).thenReturn(sessionAttributes);

        // Keskustelu alkaa
        this.stateController.popClientFromQueue(channelId, this.accessor);
        // Ketään ei jonossa
        assertEquals(0, this.queueService.getQueueLength());
        // Asiakkaan tila on chat
        assertEquals("chat", userInQueue.get("state"));

        // Hoitaja sulkee keskustelun
        this.stateController.leaveChat(channelId, mockRequest, mockPrincipal);

        // Asiakkaan sessio poistetaan keskustelun sulkemisen tuloksena
        assertNull(this.sessionRepo.getSessionFromUserId(userId));
        assertNull(this.sessionRepo.getSessionFromSessionId("1111"));
    }

    @Test
    public void unAuthenticatedProCantLeaveChatProperly() throws Exception {
        Session userInQueue = joinQueue("1111", "Hammas");

        String channelId = userInQueue.get("channelId");
        String userId = userInQueue.get("userId");

        assertEquals(1, this.queueService.getQueueLength());
        assertEquals("queue", userInQueue.get("state"));

        HttpServletRequest mockRequest = new MockHttpServletRequest("1234");
        Principal mockPrincipal = new MockPrincipal("hoitaja");
        Session proSession = sessionRepo.updateSession(mockRequest, mockPrincipal);

        // Subscribetaan kirjautuva hoitaja kanavalle
        subscribeSessionToChannel(proSession, channelId);

        assertEquals("pro", proSession.get("state"));

        Mockito.when(this.accessor.getUser()).thenReturn(new MockPrincipal("hoitaja"));
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("SPRING.SESSION.ID", "1234");
        Mockito.when(this.accessor.getSessionAttributes()).thenReturn(sessionAttributes);

        // Keskustelu alkaa
        this.stateController.popClientFromQueue(channelId, this.accessor);
        // Ketään ei jonossa
        assertEquals(0, this.queueService.getQueueLength());
        // Asiakkaan tila on chat
        assertEquals("chat", userInQueue.get("state"));

        // Hoitaja sessio on kaapattu, ja yritetään poistua chatista.
        this.stateController.leaveChat(channelId, mockRequest, null);

        assertNotNull(this.sessionRepo.getSessionFromUserId(userId));
        assertNotNull(this.sessionRepo.getSessionFromSessionId("1111"));
    }

    // Apumetodeja

    public Session joinQueue(String sessionId, String category) {
        HttpServletRequest mockRequest = new MockHttpServletRequest(sessionId);
        Principal mockPrincipal = null;
        Session joiningPerson = sessionRepo
                .updateSession(mockRequest, mockPrincipal);
        joiningPerson.set("category", category);
        this.queueService.joinQueue(joiningPerson, "Anon", "Hei!");
        this.queueItems.add(joiningPerson);
        subscribeSessionToChannel(joiningPerson, joiningPerson.get("channelId"));
        return joiningPerson;
    }

    public void emptyQueue() {
        for (Session s : this.queueItems) {
            this.queueService.removeFromQueue(s.get("channelId"));
        }
    }

    public void subscribeSessionToChannel(Session session, String channelId) {
        Channel channel = this.mapper.getChannel(channelId);
        channel.addSubscriber(session);
    }

    public Session logInAsAProfessional(String username) {
        HttpServletRequest mockRequest = new MockHttpServletRequest("1234");
        Principal mockPrincipal = new MockPrincipal(username);
        Session proSession = sessionRepo.updateSession(mockRequest, mockPrincipal);
        return proSession;
    }

    /**
     * Apumetodi viestien muuntamiseski helpommin kasiteltavaan Json-muotoon.
     *
     * @param message Palvelimelta saatu vastausviesti
     * @return
     */
    public JsonObject parseStringIntoJsonObject(String message) {
        JsonParser parser = new JsonParser();
        JsonObject jsonMessage = parser.parse(message).getAsJsonObject();
        return jsonMessage;
    }

    @Configuration
    static class TestRepoInitConfig {
        @Bean
        public ConversationRepo conversationRepo() {
            return Mockito.mock(ConversationRepo.class);
        }
        @Bean
        public PersonRepo personRepo() {
            return Mockito.mock(PersonRepo.class);
        }
        @Bean
        public MessageRepo messageRepo() {
            return Mockito.mock(MessageRepo.class);
        }
    }

    /**
     * Konfiguroidaan WebSocket testiymparistoon.
     */
    @Configuration
    @EnableScheduling
    @ComponentScan(
            basePackages={"sotechat.controller",
                    "sotechat.data",
                    "sotechat.service",
                    "sotechat.domain"},
            excludeFilters = @ComponentScan.Filter(type= FilterType.ANNOTATION,
                    value = Configuration.class)
    )
    @EnableWebSocketMessageBroker
    static class TestWebSocketConfig
            extends AbstractWebSocketMessageBrokerConfigurer {

        @Autowired
        Environment env;

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/toServer").withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.enableSimpleBroker("/toClient");
        }
    }

    @Configuration
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    static class TestConfig implements
            ApplicationListener<ContextRefreshedEvent> {

        @Autowired
        private List<SubscribableChannel> channels;

        @Autowired
        private List<MessageHandler> handlers;


        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            for (MessageHandler handler : handlers) {
                if (handler instanceof SimpAnnotationMethodMessageHandler) {
                    continue;
                }
                for (SubscribableChannel channel :channels) {
                    channel.unsubscribe(handler);
                }
            }
        }
    }
}
