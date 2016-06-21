package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import sotechat.data.*;

import java.util.*;
import static sotechat.config.StaticVariables.QUEUE_BROADCAST_CHANNEL;

/** Kuuntelee WebSocket subscribe/unsubscribe -tapahtumia
 *  - pitaa kirjaa, ketka kuuntelevat mitakin kanavaa.
 *  - kun joku subscribaa QBCC kanavalle, pyytaa QueueBroadcasteria castaamaan.
 *  HUOM: Spring hajoaa, jos kaytetaan Autowired konstruktoria tassa luokassa!
 */
@Component
public class SubscribeEventListener
        implements ApplicationListener<ApplicationEvent> {

    /** Session Repository. */
    @Autowired
    private SessionRepo sessionRepo;

    /** Queue Broadcaster. */
    @Autowired
    private QueueBroadcaster queueBroadcaster;

    /** Taikoo viestien lahetyksen. */
    @Autowired
    private SimpMessagingTemplate broker;

    /** Chat Logger (broadcastaa). */
    @Autowired
    private ChatLogger chatLogger;

    /** Mapper. */
    @Autowired
    private Mapper mapper;

    /** Siirtaa tehtavat "kasittele sub" ja "kasittele unsub" oikeille
     * metodeille. Timeria kaytetaan, jotta subscribe -tapahtuma ehditaan
     * suorittamaan loppuun ennen mahdollisia broadcasteja. Ilman timeria
     * kay usein niin, etta juuri subscribannut kayttaja ei saa broadcastia.
     * @param event kaikki applikaatioEventit aktivoivat taman.
     */
    @Override
    public final void onApplicationEvent(
            final ApplicationEvent event
    ) {
        if (SessionDisconnectEvent.class == event.getClass()) {
            handleDisconnect((SessionDisconnectEvent) event);
        }

        /** Ei kaynnisteta turhia timereita muista applikaatioeventeista. */
        if (event.getClass() != SessionSubscribeEvent.class) {
            return;
        }

        /** Halutaan kasitella event, kunhan subscribe- tapahtuma on
         * suoritettu loppuun. Muutoin juuri subscribannut
         * kayttaja ei saa mahdollista broadcastia. Spring ei valitettavasti
         * salli kyseisen logiikan kirjoittamista, joten on pakko
         * kikkailla timerin kanssa. 1ms timer toimii lahes aina, mutta
         * joskus sama ongelma toistuu sillakin. Kokeillaan 10ms timerilla. */
        Timer timer = new Timer();
        int delay = 10; // milliseconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleSubscribe((SessionSubscribeEvent) event);
            }
        }, delay);
    }

    /** Kasittelee subscribe -tapahtumat.
     * @param event event
     */
    private synchronized void handleSubscribe(
            final SessionSubscribeEvent event
    ) {
        MessageHeaders headers = event.getMessage().getHeaders();

        /** Interceptor estaa subscribet, joista puuttuu sessionId.
         * Siksi allaoleva ei voi heittaa nullpointteria. */
        String sessionId = SimpMessageHeaderAccessor
                .getSessionAttributes(headers)
                .get("SPRING.SESSION.ID").toString();

        String channelIdWithPath = SimpMessageHeaderAccessor
                .getDestination(headers);
        Session session = sessionRepo.getSessionFromSessionId(sessionId);

        if (channelIdWithPath.isEmpty()) {
            return;
        }

        /** Jos subscribattu QBCC (jonotiedotuskanava), broadcastataan jono. */
        String qbcc = "/toClient/" + QUEUE_BROADCAST_CHANNEL;
        if (channelIdWithPath.equals(qbcc)) {
            queueBroadcaster.broadcastQueue();
            return;
        }

        /** Add session to list of subscribers to channel.
         * HUOM: Aktivoituu seka /queue/ etta /chat/ subscribesta. */
        String channelId = channelIdWithPath.split("/")[3];
        Channel channel = mapper.getChannel(channelId);
        channel.addSubscriber(session);
        System.out.println("########## Adding subscriber to channelId " + channelId + " , sessionId " + sessionId);
        for (String meh : channel.getActiveUserIds()) {
            System.out.println("           fodfnodfiodfidfg " + meh);
        }

        /** Jos subscribattu /chat/kanavalle */
        String chatPrefix = "/toClient/chat/";
        if (channelIdWithPath.startsWith(chatPrefix)) {
            /** Lahetetaan kanavan chat-historia kaikille subscribaajille. */
            chatLogger.broadcast(channelId, broker);
            /** Lahetetaan tieto "uusi keskustelija liittynyt kanavalle". */
            String joinInfo = "{\"join\":\"" + session.get("username") + "\"}";
            broker.convertAndSend(channelIdWithPath, joinInfo);
        }
    }

    /** Disconnect eventin kasittely.
     * @param event disconnect event
     */
    private synchronized void handleDisconnect(
            final SessionDisconnectEvent event
    ) {
        System.out.println("DISCONNECT EVENT STRING: " + event.toString());
        System.out.println("          ja id: " + event.getSessionId());
        //TODO: yhdista WS Session Id:t HTTP Session Id:hen
        //TODO: Ilmoitus "left channel" kaikille kanaville
        //TODO: remove subscribe
    }

    /** Vaaditaan dependency injektion toimimiseen tassa tapauksessa.
     * @param repo repo
     */
    private synchronized void setSessionRepo(final SessionRepo repo) {
        this.sessionRepo = repo;
    }

    /** Vaaditaan dependency injektion toimimiseen tassa tapauksessa.
     *  @return SessionRepo sessionRepo
     * */
    private synchronized SessionRepo getSessionRepo() {
        return this.sessionRepo;
    }
}