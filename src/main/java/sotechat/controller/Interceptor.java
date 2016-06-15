package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import java.security.Principal;

/**
 * Sallii/kieltaa subscribtionin kayttaja oikeuksista riippuen.
 */
public class Interceptor extends ChannelInterceptorAdapter {

    @Autowired
    private SessionRepo sessionRepo;

    /** Mitka kaikki viestit kulkevat tata kautta?
     * @param message message
     * @param channel channel
     * @return message if allowed, exception thrown otherwise
     */
    @Override
    public Message<?> preSend(
            final Message<?> message,
            final MessageChannel channel
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        System.out.println("STOMP COMMAND: " + headerAccessor.getCommand() + " :: " + message.toString());
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            Principal userPrincipal = headerAccessor.getUser();
            String sessionId = getSessionIdFrom(headerAccessor);
            String channelIdWP = headerAccessor.getDestination();
            if (!validateSubscription(userPrincipal, sessionId, channelIdWP)) {
                throw new IllegalArgumentException("Hacking attempt?");
            }
        }
        if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
            System.out.println("Joku lahetti viestin: " + message.getPayload().toString());
        }
        return message;
    }

    private boolean validateSubscription(
            Principal principal,
            String sessionId,
            String channelIdWithPath
    ) {
        System.out.println("Validate sub for " + channelIdWithPath);

        Session session = sessionRepo.getSessionObj(sessionId);
        if (session == null) {
            return false;
        }
        String[]
        if (principal != null) {
            //TODO: Validate principal corresponds to correct sessionId
        }

        if (!session.isOnChannel(channelId)) {
            return false;
        }

        return true;
    }

    /** Palauttaa sessionId:n Stringina tai tyhjan Stringin.
     * @param headerAccessor mista id kaivetaan
     * @return sessionId String
     */
    private String getSessionIdFrom(
            final StompHeaderAccessor headerAccessor
    ) {
        try {
            return headerAccessor
                    .getSessionAttributes()
                    .get("SPRING.SESSION.ID")
                    .toString();
        } catch (Exception e) {
            return "";
        }
    }
}