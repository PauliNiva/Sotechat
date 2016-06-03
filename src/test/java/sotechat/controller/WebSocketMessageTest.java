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
import sotechat.util.MsgUtil;
import sotechat.util.TestChannelInterceptor;
import sotechat.util.TestPrincipal;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Testit WebSocket-viestien lähetykselle
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        WebSocketMessageTest.TestWebSocketConfig.class,
        WebSocketMessageTest.TestConfig.class
})
public class WebSocketMessageTest {

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
    public void
    unAuthenticatedUserReceivesCorrectResponseAfterSendingMessage()
            throws Exception {
        /**
         * Luodaan mapperiin avain-arvo -pari (id:676, username:Morko), jotta
         * ChatController-luokan routeMessage-metodissa löydetään oikea
         * käyttäjä id:n perusteella, eikä metodin suoritus siis keskeydy
         * siihen, että mappperista ei löydy oikeaa käyttäjää.
         */
        mapper.mapUsernameToId("676", "Morko");

        /**
         * Simuloidaan normaalisti JavaScriptin avulla tapahtuvaa viestien
         * lähetystä clientiltä palvelimelle. Asetetaan siis arvot mille
         * kanavalle viesti lähetetään, mikä on SessionId, ja mitä StompJS:n
         * komentoa käytetään, jotta viesti voidaan lähettää(SEND).
         */
        StompHeaderAccessor headers =
                setDefaultHeadersForChannel("/toServer/DEV_CHANNEL");


        /**
         * Luodaan lähetettävä viesti, vastaa siis normaalisti JavaScriptillä
         * Json-muodossa olevaa viestiä, joka palvelimella paketoidaan
         * MsgToServer-luokan sisälle.
         */
        MsgUtil msgUtil = new MsgUtil();
        msgUtil.add("userId", "676", false);
        msgUtil.add("channelId", "DEV_CHANNEL", true);
        msgUtil.add("content", "Hei!", true);
        msgUtil.add("userName", "Morko", true);
        msgUtil.add("timeStamp", "Sunnuntai", true);
        /**
         * Rakennetaan vielä edellä muodostetusta viestistä Message-olio,
         * joka voidaankin sitten lähettää palvelimelle.
         */
        String messageToBeSendedAsJsonString = msgUtil.mapToString();
        Message<String> message = MessageBuilder
                .createMessage(messageToBeSendedAsJsonString,
                headers.getMessageHeaders());
        /**
         * Lähetetään viesti palvelimelle.
         */
        this.clientInboundChannel.send(message);
        /**
         * Talletetaan palvelimelta tullut vastaus Message-olioon. Eli siis
         * mitä ChatControllerin routeMessage-metodi palauttaa(MsgToClient).
         */
        Message<?> reply = this.brokerChannelInterceptor.awaitMessage(5);

        /**
         * Parsetaan vastauksena saatu string JsonObjectiksi.
         */
        JsonObject jsonMessage = parseMessageIntoJsonObject(reply);

        /**
         * Tarkistetaan, että vastauksena tullut JsonObject sisältää
         * oikeat kentät, eli userName, timeStamp, content ja channelId,
         * mutta ei userId:ta! Niiden kenttien, jotka MsgUtil-olion avulla
         * on aiemmin asetettu falseksi ei pitäisi löytyä jsonMessagesta.
         */
        for (Map.Entry entry : jsonMessage.entrySet()) {
            String key = entry.getKey().toString();
            assertTrue(msgUtil.getMsgUtilSet().contains(key));
        }
    }

    @Test
    public void authenticatedRegistereUserReceivesCorrectResponse()
            throws Exception {
        StompHeaderAccessor headers =
                setDefaultHeadersForChannel("/toServer/DEV_CHANNEL");
        /**
         * Simuloidaan hoitajan kirjautumista.
         */
        headers.setUser(new TestPrincipal("hoitaja"));

        MsgUtil msgUtil = new MsgUtil();
        msgUtil.add("userId", "666", false);
        msgUtil.add("channelId", "DEV_CHANNEL", true);
        msgUtil.add("content", "Hei!", true);
        msgUtil.add("userName", "hoitaja", true);
        msgUtil.add("timeStamp", "Sunnuntai", true);

        String messageToBeSendedAsJsonString = msgUtil.mapToString();
        Message<String> message = MessageBuilder
                .createMessage(messageToBeSendedAsJsonString,
                headers.getMessageHeaders());

        this.clientInboundChannel.send(message);

        Message<?> reply = this.brokerChannelInterceptor.awaitMessage(5);

        JsonObject jsonMessage = parseMessageIntoJsonObject(reply);

        for (Map.Entry entry : jsonMessage.entrySet()) {
            String key = entry.getKey().toString();
            assertTrue(msgUtil.getMsgUtilSet().contains(key));
        }
    }

    @Test
    public void unAuthenticatedRegisteredUserDoesNotReceiveResponse()
        throws Exception {
        StompHeaderAccessor headers =
                setDefaultHeadersForChannel("/toServer/DEV_CHANNEL");

        MsgUtil msgUtil = new MsgUtil();
        msgUtil.add("userId", "666", false);
        msgUtil.add("channelId", "DEV_CHANNEL", true);
        msgUtil.add("content", "Hei!", true);
        msgUtil.add("userName", "hoitaja", true);
        msgUtil.add("timeStamp", "Sunnuntai", true);

        String messageToBeSendedAsJsonString = msgUtil.mapToString();
        Message<String> message = MessageBuilder
                .createMessage(messageToBeSendedAsJsonString,
                        headers.getMessageHeaders());

        this.clientInboundChannel.send(message);

        Message<?> reply = this.brokerChannelInterceptor.awaitMessage(5);
        /**
         * Ei pitäisi tulla vastausta, koska rekisteröityneellä käyttäjällä
         * eli hoitajalla ei ole Principal-statusta eli hän ei ole kirjautunut.
         */
        assertNull(reply);
    }

    @Test
    public void serverDoesntAcceptMessageFromUserIfUserIdDoesntExist()
        throws Exception {
        StompHeaderAccessor headers =
                setDefaultHeadersForChannel("/toServer/DEV_CHANNEL");

        MsgUtil msgUtil = new MsgUtil();
        msgUtil.add("userId", "243", false);
        msgUtil.add("channelId", "DEV_CHANNEL", true);
        msgUtil.add("content", "Hei!", true);
        msgUtil.add("userName", "hoitaja", true);
        msgUtil.add("timeStamp", "Sunnuntai", true);

        String messageToBeSendedAsJsonString = msgUtil.mapToString();
        Message<String> message = MessageBuilder
                .createMessage(messageToBeSendedAsJsonString,
                        headers.getMessageHeaders());

        this.clientInboundChannel.send(message);

        Message<?> reply = this.brokerChannelInterceptor.awaitMessage(5);
        /**
         * Ei pitäisi tulla vastausta, koska userId:tä ei löydy.
         */
        assertNull(reply);
    }

    /**
     * Asetetaan palvelimelle WebSocketin kautta lähetettävän viestin
     * headereille oletusarvot. Apumetodi joka vähentää copy-pastea.
     *
     * @param channel Tähän tulee se kanava, joka kontrolleri-metodissa
     *                on merkitty MessageMapping-annotaatiolla, esim.
     *                /toServer/{channelId}
     * @return
     */
    public StompHeaderAccessor setDefaultHeadersForChannel(String channel) {
        StompHeaderAccessor headers = StompHeaderAccessor
                .create(StompCommand.SEND);
        headers.setDestination("/toServer/DEV_CHANNEL");
        headers.setSessionId("0");
        headers.setNativeHeader("channelId", "DEV_CHANNEL");
        headers.setSessionAttributes(new HashMap<String, Object>());
        return headers;
    }

    /**
     * Apumetodi viestien muuntamiseski helpommin käsiteltävään Json-muotoon.
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
