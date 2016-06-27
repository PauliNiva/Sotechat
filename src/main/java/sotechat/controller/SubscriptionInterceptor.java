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
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(acc.getCommand())) {
            String error = validatorService.validateSubscription(acc);
            if (!error.isEmpty()) {
                String descriptivePrefix = "Subscription hacking attempt? ";
                throw new IllegalArgumentException(descriptivePrefix + error);
            }
        }

        /* Sallitaan subscriptionin normaali kasittely. */
        return message;
    }
}