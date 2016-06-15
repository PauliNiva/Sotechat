package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import java.security.Principal;

import static sotechat.config.StaticVariables.QUEUE_BROADCAST_CHANNEL;

/**
 * Sallii/kieltaa subscribtionin kayttaja oikeuksista riippuen.
 */
@Component
public class SubscriptionInterceptor extends ChannelInterceptorAdapter {

    /** Session repo. */
    private SessionRepo sessionRepo;

    /** Konstruktori.
     * @param pSessionRepo session repo.
     */
    @Autowired
    public SubscriptionInterceptor(final SessionRepo pSessionRepo) {
        sessionRepo = pSessionRepo;
    }

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
        //System.out.println("STOMP COMMAND: " + headerAccessor.getCommand() + " :: " + message.toString());
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            Principal userPrincipal = headerAccessor.getUser();
            String sessionId = getSessionIdFrom(headerAccessor);
            String channelIdWP = headerAccessor.getDestination();
            String error = validateSubscription(
                    userPrincipal, sessionId, channelIdWP);
            if (!error.isEmpty()) {
                throw new IllegalArgumentException("Hacking attempt? " + error);
            }
        }

        /** Myos viestien lahetyksen voisi sallia/estaa taalla, mutta
         * se tehdaan ChatControllerissa. Esim:
         * if (StompCommand.SEND.equals(headerAccessor.getCommand())) ... */


        return message;
    }

    /** Sallitaanko subscription?.
     * Jos sallitaan, palauttaa tyhjan Stringin.
     * Jos ei sallita, palauttaa virheilmoituksen.
     * @param principal autentikaatiotiedot
     * @param sessionId sessioId
     * @param channelIdWithPath channelIdWithPath
     * @return virheilmoitus Stringina jos ei sallita pyyntoa.
     */
    private String validateSubscription(
            final Principal principal,
            final String sessionId,
            final String channelIdWithPath
    ) {
        String prefix = "Validate subscription for channel " + channelIdWithPath
                + " by session id " + sessionId + " ### ";

        /** Kelvollinen sessio? */
        Session session = sessionRepo.getSessionObj(sessionId);
        if (session == null) {
            return prefix + "Session is null";
        }

        /** Ammattilaiskayttaja? */
        if (session.isPro()) {
            /** Loytyyko autentikaatio myos principal-oliosta? */
            if (principal == null) {
                return prefix + "Session belongs to pro but user not auth'd";
            }
            String qbcc = "/toClient/" + QUEUE_BROADCAST_CHANNEL;
            if (channelIdWithPath.equals(qbcc)) {
                /** Sallitaan - ammattilaiskayttaja saa kuunnella QBCC. */
                return "";
            }
            if (channelIdWithPath.startsWith("/toClient/queue/")) {
                /** Sallitaan hoitajien kuunnella kaikkia jonokanavia. */
                return "";
            }
        }

        /** Kielletaan subscribaus kaikkialle poislukien:
         * /toClient/queue/channelId
         * /toClient/chat/channelId
         * Tarkistetaan, onko kayttajalla/pro:lla oikeutta kanavalle.
         * TODO: Refaktoroi regexilla. */
        String[] splitted = channelIdWithPath.split("/");
        if (splitted.length != 4) {
            return prefix + "Invalid channel path (1): " + channelIdWithPath;
        }
        if (!"toClient".equals(splitted[1])) {
            return prefix + "Invalid channel path (2): " + channelIdWithPath;
        }
        if (!"queue".equals(splitted[2])
                && !"chat".equals(splitted[2])) {
            return prefix + "Invalid channel path (3): " + channelIdWithPath;
        }
        String channelId = splitted[3];

        if (session.isOnChannel(channelId)) {
            /** Sessiolla on oikeus kuunnella kanavaa. */
            return "";
        }

        return prefix + "Ei oikeutta kuunnella kanavaa!";
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