package sotechat.service;

import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import sotechat.controller.QueueBroadcaster;
import sotechat.data.Mapper;
import sotechat.data.SessionRepo;

import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class QueueTimeOutServiceTest {

    @Mock
    Mapper mapper;

    @Mock
    SessionRepo sessionRepo;

    @Mock
    QueueBroadcaster queueBroadcaster;

    @InjectMocks
    QueueTimeoutService queueTimeoutService = new QueueTimeoutService();

    @Test
    public void testCantRemoveNonExistentSessionFromQueue() {
        Mockito.when(this.sessionRepo.getSessionFromSessionId(Mockito.anyString()))
                .thenReturn(null);

        this.queueTimeoutService.removeInactiveUsersFromQueue("abc");
        Mockito.verify(this.sessionRepo, times(0)).leaveChannel(Mockito.anyString(), Mockito.anyString());
    }
    
}
