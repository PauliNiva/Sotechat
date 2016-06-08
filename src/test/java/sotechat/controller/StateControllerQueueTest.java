package sotechat.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
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
import sotechat.data.SessionRepoImpl;
import sotechat.util.MsgUtil;
import sotechat.util.TestChannelInterceptor;
import sotechat.util.TestPrincipal;
import sotechat.util.TestSession;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Testit chattiin kirjoitettujen viestien kasittelyyn ja kuljetukseen.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        StateControllerQueueTest.TestWebSocketConfig.class,
        StateControllerQueueTest.TestConfig.class
})
public class StateControllerQueueTest {

    private MapperImpl mapper;

    private SessionRepoImpl sessionRepo;

    @Autowired
    ApplicationContext context;

    @Autowired
    private AbstractSubscribableChannel clientInboundChannel;

    @Autowired
    private AbstractSubscribableChannel brokerChannel;

    private TestChannelInterceptor brokerChannelInterceptor;


    @Before
    public void setUp() throws Exception {
        this.mapper = (MapperImpl) context.getBean("mapperImpl");
        this.sessionRepo = (SessionRepoImpl) context.getBean("sessionRepoImpl");
        this.brokerChannelInterceptor = new TestChannelInterceptor();
        this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
    }

    @Test
    public void professionalCanPopUserFromQueue()
            throws Exception {
        StompHeaderAccessor headers =
                setDefaultHeadersForChannel("/toServer/queue/DEV_CHANNEL");
        /**
         * Luodaan hoitajalle sessio.
         */
        sessionRepo.mapHttpSessionToSessionId("1234", new TestSession());
        /**
         * Simuloidaan hoitajan kirjautumista.
         */
        headers.setUser(new TestPrincipal("Hoitaja"));

        /**
         * Simuloidaan sitä, että painaa "ota ensimmäinen jonosta" -nappia.
         */
        MsgUtil msgUtil = new MsgUtil();
        msgUtil.add("random", "random", true);

        String messageToBeSendedAsJsonString = msgUtil.mapToString();
        Message<byte[]> messageToBeSended = MessageBuilder
                .createMessage(messageToBeSendedAsJsonString.getBytes(),
                headers.getMessageHeaders());

        this.clientInboundChannel.send(messageToBeSended);

        Message<?> reply = this.brokerChannelInterceptor.awaitMessage(5);

        JsonObject jsonMessage = parseMessageIntoJsonObject(reply);

        assertEquals("channel activated. request new state now.",
                jsonMessage.get("content").getAsString());
    }

    @Test
    public void unAuthenticatedUserCantPopUserFromQueue() throws Exception {
        StompHeaderAccessor headers =
                setDefaultHeadersForChannel("/toServer/queue/DEV_CHANNEL");
        sessionRepo.mapHttpSessionToSessionId("1234", new TestSession());

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
        assertEquals("", replyPayload);
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
        HashMap<String, Object> sessionAttributes = new HashMap();
        sessionAttributes.put("SPRING.SESSION.ID", "1234");
        headers.setSessionAttributes(sessionAttributes);
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

    /**
     * Konfiguroidaan WebSocket testiymparistoon.
     */
    @Configuration
    @EnableScheduling
    @ComponentScan(
            basePackages="sotechat",
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
