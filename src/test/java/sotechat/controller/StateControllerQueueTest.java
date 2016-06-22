package sotechat.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
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


import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

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
    private AbstractSubscribableChannel clientInboundChannel;

    @Autowired
    private AbstractSubscribableChannel brokerChannel;

    private MockChannelInterceptor brokerChannelInterceptor;


    @Before
    public void setUp() throws Exception {
        Mockito.when(personRepo.findOne(any(String.class)))
                .thenReturn(new Person());
        Mockito.when(conversationRepo.findOne(any(String.class)))
                .thenReturn(new Conversation());
        this.mapper = (Mapper) context.getBean("mapper");
        this.queueService = (QueueService) context.getBean("queueService");

        this.mapper.mapProUsernameToUserId("hoitaja", "666");
        this.sessionRepo = (SessionRepo) context.getBean("sessionRepo");
        this.brokerChannelInterceptor = new MockChannelInterceptor();
        this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
    }

    // TODO: testi onnistuneelle popqueuelle

    @Test
    public void professionalCanPopFromQueueWebSocketTest()
            throws Exception {
        // Joinataan asiakkaana
        Session joiningPerson = joinQueue();

        String channelId = joiningPerson.get("channelId");
        // Subscribetaan samalle kanavalle kuin SetUpissa joinannut asiakas.
        StompHeaderAccessor headers =
                setDefaultHeadersForChannel("/toServer/queue/" + channelId);
        /**
         * Luodaan hoitajalle sessio.
         */
        HttpServletRequest mockRequest = new MockHttpServletRequest("1234");
        Principal mockPrincipal = new MockPrincipal("hoitaja");
        sessionRepo.updateSession(mockRequest, mockPrincipal);

        /**
         * Simuloidaan hoitajan kirjautumista.
         */
        headers.setUser(mockPrincipal);

        /**
         * Simuloidaan sitä, että painaa "ota ensimmäinen jonosta" -nappia.
         */
        MsgUtil msgUtil = new MsgUtil();
        msgUtil.add("random", "random", true);

        String messageToBeSendedAsJsonString = msgUtil.mapToString();
        Message<byte[]> messageToSend = MessageBuilder
                .createMessage(messageToBeSendedAsJsonString.getBytes(),
                headers.getMessageHeaders());

        this.clientInboundChannel.send(messageToSend);

        Message<?> reply = this.brokerChannelInterceptor.awaitMessage(5);

        String replyAsString = new String((byte[]) reply.getPayload(),
                Charset.forName("UTF-8"));

        JsonObject jsonMessage = parseStringIntoJsonObject(replyAsString);

        JsonObject queueParameters = parseStringIntoJsonObject(jsonMessage
                .get("jono").getAsJsonArray().get(0).toString());

        assertEquals(channelId, queueParameters.get("channelId").getAsString());
        assertEquals("Anon", queueParameters.get("username").getAsString());
        assertEquals("Kategoria", queueParameters.get("category").getAsString());
    }

    @Test
    public void unAuthenticatedUserCantPopUserFromQueue() throws Exception {
        Session joiningPerson = joinQueue();

        String channelId = joiningPerson.get("channelId");
        // Subscribetaan samalle kanavalle kuin SetUpissa joinannut asiakas.
        StompHeaderAccessor headers =
                setDefaultHeadersForChannel("/toServer/queue/" + channelId);

        HttpServletRequest mockRequest = new MockHttpServletRequest("1234");
        Principal principal = null;
        sessionRepo.updateSession(mockRequest, principal);

        MsgUtil msgUtil = new MsgUtil();
        msgUtil.add("random", "random", true);

        String messageToBeSendedAsJsonString = msgUtil.mapToString();
        Message<byte[]> messageToBeSended = MessageBuilder
                .createMessage(messageToBeSendedAsJsonString.getBytes(),
                        headers.getMessageHeaders());

        this.clientInboundChannel.send(messageToBeSended);

        Message<?> reply = this.brokerChannelInterceptor.awaitMessage(5);
        String replyPayload = new String((byte[]) reply.getPayload(),
                Charset.forName("UTF-8"));

        /**
         * Tyhjä vastaus, koska kirjautumaton käyttäjä ei voi ottaa toista
         * käyttäjää jonosta.
         */
        System.out.println(replyPayload);
        assertEquals(replyPayload.length(), 0);
    }

    /**
     * Asetetaan palvelimelle WebSocketin kautta lahetettavan viestin
     * headereille oletusarvot. Apumetodi joka vahentaa copy-pastea.
     *
     * @param channel Tahan tulee se kanava, joka kontrolleri-metodissa
     *                on merkitty MessageMapping-annotaatiolla, esim.
     *                /toServer/{channelId}
     * @return
     */
    public StompHeaderAccessor setDefaultHeadersForChannel(String channel) {
        StompHeaderAccessor headers = StompHeaderAccessor
                .create(StompCommand.SEND);
        headers.setDestination(channel);
        headers.setSessionId("0");
        headers.setNativeHeader("channelId", "DEV_CHANNEL");
        HashMap<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("SPRING.SESSION.ID", "1234");
        headers.setSessionAttributes(sessionAttributes);
        return headers;
    }

    public Session joinQueue() {
        HttpServletRequest mockRequest = new MockHttpServletRequest("1111");
        Principal mockPrincipal = null;
        Session joiningPerson = sessionRepo
                .updateSession(mockRequest, mockPrincipal);

        this.queueService.joinQueue(joiningPerson, "Anon", "Hei!");
        return joiningPerson;
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
                    "sotechat.domainService",
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
