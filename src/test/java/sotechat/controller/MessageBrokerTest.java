package sotechat.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.util.MockPrincipal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
/*
public class MessageBrokerTest {

    SessionRepo sessionRepo;
    SimpMessagingTemplate template;
    MessageBroker broker;

    @Before
    public void setUp() {
        Session session = new Session();
        session.addChannel("testikanava");
        Mapper mapper = Mockito.mock(Mapper.class);
        sessionRepo = Mockito.mock(SessionRepo.class);
        Mockito.when(sessionRepo.getSessionFromUserId(any()))
                .thenReturn(session);
        template = Mockito.mock(SimpMessagingTemplate.class);
        broker = new MessageBroker(mapper, sessionRepo, template);
        Mockito.when(mapper.getIdFromRegisteredName("Mikko"))
                .thenReturn(session.get("userId"));
        Mockito.doNothing().when(template).convertAndSend(anyString(),anyString());
    }

    @Test
    public void test() {
        MockPrincipal principal = new MockPrincipal("Mikko");
        broker.sendJoinLeaveNotices(principal, "true");
        Mockito.verify(template, times(1)).convertAndSend(anyString(), anyString());
        broker.sendJoinLeaveNotices(principal, "false");
        Mockito.verify(template, times(2)).convertAndSend(anyString(), anyString());
    }
}
*/