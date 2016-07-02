package sotechat.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import sotechat.controller.QueueBroadcaster;
import sotechat.controller.QueueBroadcasterImpl;
import sotechat.service.QueueService;
import sotechat.service.ValidatorService;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;

public class QueueBroadcasterTest {

    QueueBroadcaster qbc;
    MessageBroker broker;

    @Before
    public void setUp() {
        QueueService qService = Mockito.mock(QueueService.class);
        broker = Mockito.mock(MessageBroker.class);
        Mockito.when(qService.toString()).thenReturn("jono");
        qbc = new QueueBroadcasterImpl(qService, broker);
    }

    @Test
    public void qbcTest() {
        qbc.broadcastQueue();
        Mockito.verify(broker).convertAndSend(any(String.class), any(String.class));
        qbc.broadcastQueue();
        Mockito.verify(broker, timeout(10)
                .times(1)).convertAndSend(any(String.class), any(String.class));
        Mockito.verify(broker, timeout(QueueBroadcasterImpl.QBC_DELAY_MS)
                .times(1)).convertAndSend(any(String.class), any(String.class));

    }
}
