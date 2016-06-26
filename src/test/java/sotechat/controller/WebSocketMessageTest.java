package sotechat.controller;

import com.google.gson.*;
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
import org.springframework.messaging.simp.annotation.support
        .SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.config.annotation
        .AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation
        .EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import sotechat.data.*;
import sotechat.domain.Conversation;
import sotechat.repo.ConversationRepo;;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;
import sotechat.util.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyString;

/**
 * Testit chattiin kirjoitettujen viestien kasittelyyn ja kuljetukseen.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        WebSocketMessageTest.TestWebSocketConfig.class,
        WebSocketMessageTest.TestConfig.class,
        WebSocketMessageTest.TestRepoInitConfig.class
})
public class WebSocketMessageTest {

    private Mapper mapper;

    private SessionRepo sessionRepo;

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private ConversationRepo conversationRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    ApplicationContext context;

    @Autowired
    private AbstractSubscribableChannel clientInboundChannel;

    @Autowired
    private AbstractSubscribableChannel brokerChannel;

    private MockChannelInterceptor brokerChannelInterceptor;


    @Before
    public void setUp() throws Exception {
        Mockito.when(conversationRepo.findOne(anyString()))
                .thenReturn(new Conversation());
        this.mapper = (Mapper) context.getBean("mapper");
        this.sessionRepo = (SessionRepo) context.getBean("sessionRepo");
        this.mapper.mapProUsernameToUserId("666", "hoitaja");
        this.brokerChannelInterceptor = new MockChannelInterceptor();
        this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
    }

    @After
    public void tearDown() {
        this.sessionRepo.forgetSessions();
    }

    @Test
    public void testMessageCanBeSendedAndReceivedThroughWebSockets()
            throws Exception {

        HttpServletRequest mockRequest = new MockHttpServletRequest("0");
        Session session = sessionRepo.updateSession(mockRequest, null);
        String username = session.get("username");
        String userId = session.get("userId");
        String channelId = session.get("channelId");
        subscribeSessionToChannel(session, channelId);
        /**
         * Simuloidaan normaalisti JavaScriptin avulla tapahtuvaa viestien
         * lahetysta clientilta palvelimelle. Asetetaan siis arvot mille
         * kanavalle viesti lahetetaan, mika on SessionId, ja mita StompJS:n
         * komentoa kaytetaan, jotta viesti voidaan lahettaa(SEND).
         */
        StompHeaderAccessor headers =
                setDefaultHeadersForChannel("/toServer/chat/DEV_CHANNEL");

        /**
         * Luodaan lahetettava viesti, vastaa siis normaalisti JavaScriptilla
         * Json-muodossa olevaa viestia, joka palvelimella paketoidaan
         * MsgToServer-luokan sisalle.
         */
        MsgUtil msgUtil = new MsgUtil();
        msgUtil.add("messageId", "123", true);
        msgUtil.add("userId", userId, false);
        msgUtil.add("channelId", channelId, true);
        msgUtil.add("timeStamp", "sunnuntai", true);
        msgUtil.add("username", username, true);
        msgUtil.add("content", "Hei!", true);

        /**
         * Rakennetaan viela edella muodostetusta viestista Message-olio,
         * joka voidaankin sitten lahettaa palvelimelle.
         */
        String messageToBeSendedAsJsonString = msgUtil.mapToString();
        Message<byte[]> message = MessageBuilder
                .createMessage(messageToBeSendedAsJsonString.getBytes(),
                headers.getMessageHeaders());

        /**
         * Lahetetaan viesti palvelimelle.
         */
        this.clientInboundChannel.send(message);

        /**
         * Talletetaan palvelimelta tullut vastaus Message-olioon. Eli siis
         * mita ChatControllerin routeMessage-metodi palauttaa(MsgToClient).
         */
        Message<?> reply = this.brokerChannelInterceptor.awaitMessage(5);

        /**
         * Parsetaan vastauksena saatu string JsonObjectiksi.
         */
        JsonObject jsonMessage = parseStringIntoJsonObject(reply);

        /**
         * Tarkistetaan, etta vastauksena tullut JsonObject sisaltaa
         * oikeat kentat, eli username, timeStamp, content ja channelId,
         * mutta ei userId:ta! Niiden kenttien, jotka MsgUtil-olion avulla
         * on aiemmin asetettu falseksi ei pitaisi loytya jsonMessagesta.
         */
        for (Map.Entry entry : jsonMessage.entrySet()) {
            String key = entry.getKey().toString();
            assertTrue(msgUtil.getMsgUtilSet().contains(key));
        }
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
        sessionAttributes.put("SPRING.SESSION.ID", "0");
        headers.setSessionAttributes(sessionAttributes);
        return headers;
    }

    /**
     * Apumetodi viestien muuntamiseski helpommin kasiteltavaan Json-muotoon.
     *
     * @param message Palvelimelta saatu vastausviesti
     * @return
     */
    public JsonObject parseStringIntoJsonObject(Message<?> message) {
        String json = new String((byte[]) message.getPayload(),
                Charset.forName("UTF-8"));
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonObject();
    }

    public void subscribeSessionToChannel(Session session, String channelId) {
        Channel channel = this.mapper.getChannel(channelId);
        channel.addSubscriber(session);
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
                    value = {Configuration.class})
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
