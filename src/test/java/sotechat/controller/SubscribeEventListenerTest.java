package sotechat.controller;


import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import sotechat.data.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Luokkaa testaa subscribejen kasittelya SubscribeEventListenerissa
 * (eli sen jalkeen, kun subscribe on jo validoitu Interceptorissa).
 */
public class SubscribeEventListenerTest {

    SubscribeEventListener listener;
    QueueBroadcaster qbc;
    ChatLogger chatLogger;
    MessageBroker broker;
    SessionRepo sessionRepo;
    Mapper mapper;
    Channel channel;

    @Before
    public void setUp() {
        listener = new SubscribeEventListener();

        /* Mockataan riippuvuudet. */
        qbc = mock(QueueBroadcasterImpl.class);
        chatLogger = mock(ChatLogger.class);
        broker = mock(MessageBroker.class);
        sessionRepo = mock(SessionRepo.class);
        mapper = mock(Mapper.class);
        listener.setQueueBroadcaster(qbc);
        listener.setChatLogger(chatLogger);
        listener.setBroker(broker);
        listener.setSessionRepo(sessionRepo);
        listener.setMapper(mapper);
        channel = mock(Channel.class);

        when(sessionRepo.getSessionFromSessionId(any())).thenReturn(new Session());
        when(mapper.getChannel(any())).thenReturn(channel);
    }

    /**
     * Testaa, etta ei kaadu vaarantyyppiseen applikaatioeventtiin.
     */
    @Test
    public void eventListenerDoesNotThrowExceptionOnIncorrectTypeEventTest() {
        SessionUnsubscribeEvent event = mock(SessionUnsubscribeEvent.class);
        listener.onApplicationEvent(event);
    }

    /**
     * Testaa eventListener tyypillisella subscribe /chat/ eventilla.
     */
    @Test
    public void eventListenerValidSubscribeChatTest() {
        subscribeTo("/toClient/chat/oifsdfio");
        assertChatLoggerBroadcast(1);
        assertSubscriberAddedToChannel(1);
        assertQueueBroadcast(0);
    }

    /**
     * Testaa eventListener tyypillisella subscribe /queue/ eventilla.
     */
    @Test
    public void eventListenerValidSubscribeQueueTest() {
        subscribeTo("/toClient/queue/oifsdfio");
        assertChatLoggerBroadcast(0);
        assertSubscriberAddedToChannel(1);
        assertQueueBroadcast(0);
    }

    /**
     * Testaa eventListener tyypillisella subscribe /QBCC/ eventilla.
     */
    @Test
    public void eventListenerValidSubscribeQBCCTest() {
        subscribeTo("/toClient/QBCC");
        assertChatLoggerBroadcast(0);
        assertSubscriberAddedToChannel(0);
        assertQueueBroadcast(1);
    }

    /**
     * Testaa eventListener tyhjan polun subscribe eventilla.
     */
    @Test
    public void eventListenerEmptySubscribeTest() {
        subscribeTo("");
        assertChatLoggerBroadcast(0);
        assertSubscriberAddedToChannel(0);
        assertQueueBroadcast(0);
    }

    /**
     * Asserts that chatLogger's broadCast method gets called *count* times.
     * @param count number of times
     */
    public void assertChatLoggerBroadcast(int count) {
        verify(chatLogger, timeout(50).times(count)).broadcast(any(), any());
    }

    public void assertSubscriberAddedToChannel(int count) {
        verify(channel, timeout(50).times(count)).addSubscriber(any());
    }

    public void assertQueueBroadcast(int count) {
        verify(qbc, timeout(50).times(count)).broadcastQueue();
    }

    /**
     * Nikkaroi subscribeEventin ja antaa sen listenerille.
     * @param path String polku johon subscribetaan
     */
    private void subscribeTo(String path) {
        Map<String, List<String>> nativeHeaders = new HashMap<>();
        nativeHeaders.put("id", Collections.singletonList("sub-0"));
        nativeHeaders.put("destination", Collections.singletonList(path));
        Map<String, String> springSessionMap = new HashMap<>();
        springSessionMap.put("SPRING.SESSION.ID", "533A5A285E4E69BEBF4A61CF24B363B5");
        Message<byte[]> msg = MessageBuilder.withPayload("test".getBytes())
                .setHeader("simpMessageType", SimpMessageType.SUBSCRIBE)
                .setHeader("stompCommand", StompCommand.SUBSCRIBE)
                .setHeader(NativeMessageHeaderAccessor.NATIVE_HEADERS, nativeHeaders)
                .setHeader("simpSessionAttributes", springSessionMap)
                .setHeader("simpHeartbeat", "[J@2b67932")
                .setHeader("simpSubscriptionId", "sub-0")
                .setHeader("simpSessionId", "t049m7e9")
                .setHeader("simpDestination", path)
                .build();

        SessionSubscribeEvent event = new SessionSubscribeEvent("test", msg);
        listener.getSessionRepo();
        listener.onApplicationEvent(event);
    }
}
