package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;
import sotechat.service.ValidatorService;
import java.security.Principal;


/**
 * Sallii/kieltaa subscriptionin kayttajaoikeuksista riippuen.
 * Jos Interceptoria ei ole, kuka tahansa voi subscribaa esimerkiksi
 * kanavalle /toClient/* ja siten kuunnella salaa kaikkien viesteja.
 */
@Component
public class SubscriptionInterceptor extends ChannelInterceptorAdapter {

    /** Validator Service suorittaa validointilogiikan. */
    private ValidatorService validatorService;

    /** Konstruktori.
     * @param pValidatorService validatorService
     */
    @Autowired
    public SubscriptionInterceptor(
            final ValidatorService pValidatorService
    ) {
        validatorService = pValidatorService;
    }

    /** Toimii "portinvartijana" subscribe-tapahtumille.
     * @param message message
     * @param channel channel
     * @return message jos sallitaan subscribe. palautusarvo toimii tassa
     * tapauksessa niin, etta subscribe-viestin kulkeminen sita kasitteleville
     * metodeille sallitaan. Jos ei sallita, heitetaan poikkeus.
     */
    @Override
    public final Message<?> preSend(
            final Message<?> message,
            final MessageChannel channel
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            Principal userPrincipal = headerAccessor.getUser();
            String sessionId = getSessionIdFrom(headerAccessor);
            String channelIdWP = headerAccessor.getDestination();
            String error = validatorService.validateSubscription(
                    userPrincipal, sessionId, channelIdWP);
            if (!error.isEmpty()) {
                throw new IllegalArgumentException("Hacking attempt? " + error);
            }
        }

        return message;
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