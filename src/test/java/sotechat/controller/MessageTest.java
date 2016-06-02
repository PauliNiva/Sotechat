package sotechat.controller;

import com.google.gson.*;
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
import sotechat.data.MapperImpl;
import sotechat.util.Morko;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Testit WebSocket-viestien lähetykselle
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        MessageTest.TestWebSocketConfig.class,
        MessageTest.TestConfig.class
})
public class MessageTest {

    private MapperImpl mapper;

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
        this.brokerChannelInterceptor = new TestChannelInterceptor();
        this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
    }

    @Test
    public void clientReceivesCorrectResponseAfterSendingMessage()
            throws Exception {
        /**
         * Luodaan mapperiin avain-arvo -pari (id:676, username:Morko), jotta
         * ChatController-luokan routeMessage-metodissa löydetään oikea
         * käyttäjäid:n perusteella, ja metodi kyetään suorittamaan
         * loppuun saakka.
         */
        mapper.mapUsernameToId("676", "Morko");

        /**
         * Simuloidaan normaalisti JavaScriptin avulla tapahtuvaa viestien
         * lähetystä clientiltä palvelimelle. Asetetaan siis arvot mille
         * kanavalle viesti lähetetään, mikä on SessionId, ja mitä StompJS:n
         * komentoa käytetään, jotta viesti voidaan lähettää(SEND).
         */
        StompHeaderAccessor headers = StompHeaderAccessor
                .create(StompCommand.SEND);
        headers.setDestination("/toServer/DEV_CHANNEL");
        headers.setSessionId("0");
        headers.setNativeHeader("channelId", "DEV_CHANNEL");
        headers.setSessionAttributes(new HashMap<String, Object>());

        /**
         * Luodaan lähetettävä viesti, vastaa siis normaalisti JavaScriptillä
         * Json-muodossa generoitavaa luokkaa, josta
         */
        Morko morko = new Morko();
        morko.add("userId", "676", false);
        morko.add("channelId", "DEV_CHANNEL", true);
        morko.add("content", "Hei!", true);
        morko.add("userName", "Morko", true);
        morko.add("timeStamp", "Sunnuntai", true);
        /**
         * Rakennetaan vielä edellä muodostetusta viestistä Message-olio,
         * joka voidaankin sitten lähettää palvelimelle.
         */
        String jsonString = morko.mapToString();
        Message<String> message = MessageBuilder.createMessage(jsonString,
                headers.getMessageHeaders());
        /**
         * Lähetetään viesti palvelimelle.
         */
        this.clientInboundChannel.send(message);
        /**
         * Talletetaan palvelimelta tullut vastaus Message-olioon. Eli siis
         * mitä ChatControllerin routeMessage-metodi palauttaa.
         */
        Message<?> reply = this.brokerChannelInterceptor.awaitMessage(5);

        /**
         * Testi, joka tarkistaa, että palvelimelta tulleen viestin
         * headereissa oikea sisältö. Nyt testataan vain sessionId, myös
         * muita arvoja voitaisiin testata.
         */
        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());

        /**
         * Parsetaan vastauksena saatu string JsonObjectiksi.
         */
        String json = new String((byte[]) reply.getPayload(),
                Charset.forName("UTF-8"));
        JsonParser parser = new JsonParser();
        JsonObject jsonMessage = parser.parse(json).getAsJsonObject();

        /**
         * Tarkistetaan, että vastauksena tullut JsonObject sisältää
         * oikeat kentät, eli userName, timeStamp, content ja channelId,
         * mutta ei userId:ta!
         */
        for (Map.Entry entry : jsonMessage.entrySet()) {
            String key = entry.getKey().toString();
            assertTrue(morko.getMorkoSet().contains(key));
        }
    }

    /**
     * Konfiguroidaan WebSocket testiympäristöön.
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
