package sotechat.websocketService;

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
import sotechat.data.MapperImpl;
import sotechat.domain.Conversation;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;
import sotechat.util.MockChannelInterceptor;
import sotechat.util.MockPrincipal;
import sotechat.util.MsgUtil;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;

/**
 * Testit chattiin kirjoitettujen viestien kasittelyyn ja kuljetukseen.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        WebSocketDisconnectTest.TestWebSocketConfig.class,
        WebSocketDisconnectTest.TestConfig.class,
        WebSocketDisconnectTest.TestRepoInitConfig.class
})
public class WebSocketDisconnectTest {

    private MapperImpl mapper;

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
        Mockito.when(conversationRepo.findOne(any(String.class)))
                .thenReturn(new Conversation());
        this.mapper = (MapperImpl) context.getBean("mapperImpl");
        this.brokerChannelInterceptor = new MockChannelInterceptor();
        this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
    }


    @Test
    public void serverDoesntAcceptMessageFromUserIfUserIdDoesntExist()
        throws Exception {
        StompHeaderAccessor headers =
                setDefaultHeadersForChannel("/toServer/chat/DEV_CHANNEL");

        MsgUtil msgUtil = new MsgUtil();
        msgUtil.add("userId", "243", false);
        msgUtil.add("channelId", "DEV_CHANNEL", true);
        msgUtil.add("content", "Hei!", true);
        msgUtil.add("username", "Hoitaja", true);
        msgUtil.add("timeStamp", "Sunnuntai", true);

        String messageToBeSendedAsJsonString = msgUtil.mapToString();
        Message<byte[]> message = MessageBuilder
                .createMessage(messageToBeSendedAsJsonString.getBytes(),
                        headers.getMessageHeaders());

        this.clientInboundChannel.send(message);

        Message<?> reply = this.brokerChannelInterceptor.awaitMessage(5);
        /**
         * Ei pitaisi tulla vastausta, koska userId:ta ei loydy.
         */
        assertNull(reply);
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
                .create(StompCommand.DISCONNECT);
        headers.setDestination(channel);
        headers.setSessionId("0");
        headers.setNativeHeader("channelId", "DEV_CHANNEL");
        headers.setSessionAttributes(new HashMap<String, Object>());
        return headers;
    }

    /**
     * Apumetodi viestien muuntamiseski helpommin kasiteltavaan Json-muotoon.
     *
     * @param message Palvelimelta saatu vastausviesti
     * @return
     */
    public JsonObject parseMessageIntoJsonObject(Message<?> message) {
        String json = new String((byte[]) message.getPayload(),
                Charset.forName("UTF-8"));
        JsonParser parser = new JsonParser();
        JsonObject jsonMessage = parser.parse(json).getAsJsonObject();
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
