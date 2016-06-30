package sotechat.connectionEvents;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import sotechat.controller.WebSocketConnectHandler;
import sotechat.controller.MessageBroker;
import sotechat.data.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Testit chattiin kirjoitettujen viestien kasittelyyn ja kuljetukseen.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WebSocketConnectHandlerTest {

    @Mock
    private SessionRepo sessionRepo;

    @Spy
    private MessageBroker broker;

    @InjectMocks
    private WebSocketConnectHandler webSocketConnectHandler = new WebSocketConnectHandler();

    @Test
    public void testExistingUserSessionStatusIsChangedToConnected() {
        SessionConnectEvent event = Mockito.mock(SessionConnectEvent.class);
        Message<byte[]> message = Mockito.mock(Message.class);

        MessageHeaders headers = Mockito.mock(MessageHeaders.class);
        SimpMessageHeaderAccessor accessor = Mockito.mock(SimpMessageHeaderAccessor.class);

        Map<String, Object> sessionAttributes = new HashMap();
        sessionAttributes.put("SPRING.SESSION.ID", "123");

        Mockito.when(event.getMessage()).thenReturn(message);
        Mockito.when(message.getHeaders()).thenReturn(headers);
        Mockito.when(accessor.getSessionAttributes(headers))
                .thenReturn(sessionAttributes);


        Session session = new Session();

        Mockito.when(this.sessionRepo.getSessionFromSessionId("123"))
                .thenReturn(session);

        this.webSocketConnectHandler.onApplicationEvent(event);

        assertEquals("connected", session.get("connectionStatus"));
    }
}
