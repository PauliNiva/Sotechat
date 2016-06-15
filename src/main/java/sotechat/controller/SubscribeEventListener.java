package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import sotechat.data.ChatLogger;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.service.StateService;

import java.util.*;

/** Kuuntelee WebSocket subscribe/unsubscribe -tapahtumia
 *  - pitaa kirjaa, ketka kuuntelevat mitakin kanavaa.
 *  - kun joku subscribaa QBCC kanavalle, pyytaa QueueBroadcasteria castaamaan.
 */
@Component
public class SubscribeEventListener
        implements ApplicationListener<ApplicationEvent> {

    /** Key = channelIDWithPath, value = list of subscribed sessions. */
    private HashMap<String, List<Session>> map;

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

    /** Vain 1 instanssi. */
    public SubscribeEventListener() {
        map = new HashMap<String, List<Session>>();
    }

    /** Palauttaa listan sessioita, jotka ovat subscribanneet kanavaID:lle.
     * @param channelId kanavaId
     * @return lista sessioita
     */
    public final synchronized List<Session> getSubscribers(
            final String channelId
    ) {
        List<Session> subs = map.get(channelId);
        if (subs == null) {
            subs = new ArrayList<Session>();
        }
        return subs;
    }


    /** Siirtaa tehtavat "kasittele sub" ja "kasittele unsub" oikeille
     * metodeille. Timeria kaytetaan, jotta subscribe -tapahtuma ehditaan
     * suorittamaan loppuun ennen mahdollisia broadcasteja. Ilman timeria
     * kay usein niin, etta juuri subscribannut kayttaja ei saa broadcastia.
     * @param applicationEvent kaikki applikaatioEventit aktivoivat taman.
     */
    @Override
    public final void onApplicationEvent(
            final ApplicationEvent applicationEvent
    ) {

        /** Ei kaynnisteta turhia timereita muista applikaatioeventeista. */
        if (applicationEvent.getClass() != SessionSubscribeEvent.class
            && applicationEvent.getClass() != SessionUnsubscribeEvent.class) {
            return;
        }

        /** Kaynnistetaan timer, joka kasittelee eventin, kunhan
         * on suoritettu loppuun. */
        Timer timer = new Timer();
        int delay = 1; // milliseconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                delayedEventHandling(applicationEvent);
            }
        }, delay);
    }

    /** Timerin avulla kutsuttu metodi, joka vain
     * haarauttaa sub/unsub pyynnot oikeaan metodiin.
     * @param applicationEvent
     */
    private void delayedEventHandling(
            final ApplicationEvent applicationEvent
    ) {
        if (applicationEvent.getClass() == SessionSubscribeEvent.class) {
            handleSubscribe((SessionSubscribeEvent) applicationEvent);
        } else if
                (applicationEvent.getClass() == SessionUnsubscribeEvent.class) {
            handleUnsubscribe((SessionUnsubscribeEvent) applicationEvent);
        }
    }

    /** Kasittelee subscribe -tapahtumat.
     * @param event event
     */
    private synchronized void handleSubscribe(
            final SessionSubscribeEvent event
    ) {
        //System.out.println("SUB = " + event.toString());
        MessageHeaders headers = event.getMessage().getHeaders();

        /** Interceptor estaa subscribet, joista puuttuu sessionId. */
        String sessionId = SimpMessageHeaderAccessor
                .getSessionAttributes(headers)
                .get("SPRING.SESSION.ID").toString();

        String channelIdWithPath = SimpMessageHeaderAccessor
                .getDestination(headers);
        Session session = sessionRepo.getSessionObj(sessionId);

        System.out.println("Subscribing someone to " + channelIdWithPath);
        if (channelIdWithPath.isEmpty()) {
            return;
        }

        /** Add session to list of subscribers to channelId. */
        List<Session> list = map.get(channelIdWithPath);
        if (list == null) {
            list = new ArrayList<>();
            map.put(channelIdWithPath, list);
        }
        list.add(session);

        /** Jos subscribattu QBCC (jonotiedotuskanava), tiedotetaan. */
        String qbcc = "/toClient/" + StateService.QUEUE_BROADCAST_CHANNEL;
        if (channelIdWithPath.equals(qbcc)) {
            queueBroadcaster.broadcastQueue();
        }

        /** Jos subscribattu /chat/kanavalle, lahetetaan kanavan viestihistoria
         * kaikille kanavan subscribaajille (alkuun "tyhjenna naytto" spessu) */
        String chatPrefix = "/toClient/chat/";
        if (channelIdWithPath.startsWith(chatPrefix)) {
            String channelId = channelIdWithPath.substring(chatPrefix.length());
            chatLogger.broadcast(channelId, broker);
        }
    }

    /** TODO: Kasittelee unsubscribe -tapahtumat.
     * @param event event
     */
    private synchronized void handleUnsubscribe(
            final SessionUnsubscribeEvent event
    ) {
        System.out.println("UNSUB = " + event.toString());
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
