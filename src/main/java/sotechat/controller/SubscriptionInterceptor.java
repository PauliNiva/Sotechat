package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

import sotechat.service.ValidatorService;


/**
 * Sallii/kieltaa kanavan tilauksen kayttajaoikeuksista riippuen.
 * Jos Interceptoria ei ole, kuka tahansa voi tilata esimerkiksi
 * kanavalle /toClient/* ja siten kuunnella salaa kaikkien viesteja.
 */
@Component
public class SubscriptionInterceptor extends ChannelInterceptorAdapter {

    /**
     * Validator Service suorittaa validointilogiikan.
     */
    private ValidatorService validatorService;

    /**
     * Konstruktori.
     *
     * @param pValidatorService validatorService
     */
    @Autowired
    public SubscriptionInterceptor(
            final ValidatorService pValidatorService
    ) {
        validatorService = pValidatorService;
    }

    /**
     * Toimii "portinvartijana" tilaus-tapahtumille.
     *
     * @param message message
     * @param channel channel
     * @return message Jos sallitaan tilaus. Palautusarvo toimii tassa
     * tapauksessa niin, etta tilaus-viestin kulkeminen sita kasitteleville
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

        /* Sallitaan sanoman normaali kasittely. */
        return message;
    }

}
