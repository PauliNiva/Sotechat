package sotechat.controller;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

import java.security.Principal;

/**
 * Sallii/kieltaa subscribtionin kayttaja oikeuksista riippuen.
 */
public class Interceptor extends ChannelInterceptorAdapter {

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

            String sessionId = headerAccessor.getSessionAttributes()
                    .get("SPRING.SESSION.ID").toString();
            String channelIdWP = headerAccessor.getDestination();
            if (!validateSubscription(userPrincipal, sessionId, channelIdWP)) {
                throw new IllegalArgumentException("Hacking attempt?");
            }
        }
        if (StompCommand.SEND.equals(headerAccessor.getCommand())) {

        }
        return message;
    }

    private boolean validateSubscription(
            Principal principal,
            String sessionId,
            String channelIdWithPath
    ) {
        System.out.println("Validate sub for " + channelIdWithPath);
        //TODO: Validate principal corresponds to correct sessionId
        //TODO: Person person = getPersonFromSessionId(sessionId)
        //TODO: person.isCurrentlyOnChannel(channelIdWithPath)
        return true;
    }
}