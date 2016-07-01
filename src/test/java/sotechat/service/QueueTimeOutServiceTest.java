package sotechat.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import sotechat.controller.MessageBroker;
import sotechat.controller.QueueBroadcaster;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import java.util.Timer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class QueueTimeOutServiceTest {

    @Mock
    QueueService queueService;

    @Mock
    SessionRepo sessionRepo;

    @Mock
    MessageBroker broker;

    @Mock
    QueueBroadcaster queueBroadcaster;

    @InjectMocks
    TimeoutService timeoutService = new TimeoutService();

    @Test
    public void testCantRemoveNonExistentSessionFromQueue() {
        Mockito.when(this.sessionRepo.getSessionFromSessionId(Mockito.anyString()))
                .thenReturn(null);

        this.timeoutService.processDisconnect(Mockito.anyString());
        Mockito.verify(this.sessionRepo, times(0)).leaveChannel(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(this.queueService, times(0)).removeFromQueue(Mockito.anyString());
        Mockito.verify(this.queueBroadcaster, times(0)).broadcastQueue();
    }

    @Test
    public void testDisconnectedSessionIsRemovedSuccessfully() {
        Session session = new Session();
        session.addChannel("testikanava");
        session.set("connectionStatus", "disconnected");

        Mockito
                .when(this.sessionRepo.getSessionFromSessionId(Mockito.anyString()))
                .thenReturn(session);
        Mockito.doNothing().when(this.broker).sendClosedChannelNotice(any());

        this.timeoutService.processDisconnect(Mockito.anyString());
        Mockito.verify(this.sessionRepo, times(1)).leaveChannel(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(this.queueService, times(1)).removeFromQueue(Mockito.anyString());
        Mockito.verify(this.queueBroadcaster, times(0)).broadcastQueue();
    }

    @Test
    public void testConnectedSessionIsNotRemoved() {
        Session session = new Session();
        session.set("connectionStatus", "connected");

        Mockito
                .when(this.sessionRepo.getSessionFromSessionId(Mockito.anyString()))
                .thenReturn(session);

        this.timeoutService.processDisconnect(Mockito.anyString());
        Mockito.verify(this.sessionRepo, times(0)).leaveChannel(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(this.queueService, times(0)).removeFromQueue(Mockito.anyString());
        Mockito.verify(this.queueBroadcaster, times(0)).broadcastQueue();
    }

    @Test
    public void testQueueRemovalIsInitiatedAfterWait() {
        Timer timer = Mockito.mock(Timer.class);

        this.timeoutService.setTimer(timer);

        this.timeoutService.waitThenProcessDisconnect("1");

        Mockito.verify(timer, times(1)).schedule(Mockito.anyObject(), Mockito.anyLong());
    }
}
