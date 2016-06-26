package sotechat.controller;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import sotechat.service.ValidatorService;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

/**
 * Testataan SubscribeInterceptorin toimintaa ilman palvelimen kaynnistysta.
 * Riippuvuus ValidatorServiceen mockataan.
 */
public class SubscriptionInterceptorTest {

    ValidatorService validator;

    SubscriptionInterceptor interceptor;

    Message<?> msg;

    @Before
    public void setUp() {
        validator = Mockito.mock(ValidatorService.class);
        interceptor = new SubscriptionInterceptor(validator);
    }

    /**
     * Testaa, etta kelvollisella subscribella interceptorin
     * metodi palauttaa annetun viestin (eli antaa sen jatkaa
     * matkaansa Springin sisalla varsinaiseen kasittelyyn).
     */
    @Test
    public void subscribeInterceptorDoesNotThrowExceptionOnValidSubscribeTest() {
        msg = generateSomeMessage(StompCommand.SUBSCRIBE);
        Mockito.when(validator.validateSubscription(any()))
                .thenReturn(""); // no error
        Message<?> returnValue = interceptor.preSend(msg, null);
        assertEquals(msg, returnValue);
    }

    /**
     * Testaa, etta Interceptor heittaa poikkeuksen epakelvolla subscribella.
     */
    @Test(expected=IllegalArgumentException.class)
    public void subscribeInterceptorThrowsExceptionOnInvalidSubscribeTest() {
        msg = generateSomeMessage(StompCommand.SUBSCRIBE);
        Mockito.when(validator.validateSubscription(any()))
                .thenReturn("Error");
        interceptor.preSend(msg, null);
    }

    /**
     * Testaa, etta Interceptor antaa muiden eventtien kulkea.
     */
    @Test
    public void subscribeInterceptorLetsOtherEventsPassTest() {
        msg = generateSomeMessage(StompCommand.UNSUBSCRIBE);
        Message<?> returnValue = interceptor.preSend(msg, null);
        assertEquals(msg, returnValue);
    }

    private Message<?> generateSomeMessage(StompCommand eventType) {
        Map<String, List<String>> nativeHeaders = new HashMap<>();
        nativeHeaders.put("id", Collections.singletonList("sub-0"));
        nativeHeaders.put("destination", Collections.singletonList("/toClient/queue/6pbdx57vwqosurk5"));
        return MessageBuilder.withPayload("test")
                .setHeader("simpMessageType", SimpMessageType.SUBSCRIBE)
                .setHeader("stompCommand", eventType)
                .setHeader(NativeMessageHeaderAccessor.NATIVE_HEADERS, nativeHeaders)
                .setHeader("simpSessionAttributes", "{SPRING.SESSION.ID=533A5A285E4E69BEBF4A61CF24B363B5}")
                .setHeader("simpHeartbeat", "[J@2b67932")
                .setHeader("simpSubscriptionId", "sub-0")
                .setHeader("simpSessionId", "t049m7e9")
                .setHeader("simpDestination", "/toClient/queue/6pbdx57vwqosurk5")
                .build();
    }

}