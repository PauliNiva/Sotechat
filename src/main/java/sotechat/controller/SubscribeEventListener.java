package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.data.Channel;


import java.util.Timer;
import java.util.TimerTask;

import static sotechat.config.StaticVariables.QUEUE_BROADCAST_CHANNEL;

/** Kuuntelee WebSocket subscribe/unsubscribe -tapahtumia
 *  - pitaa kirjaa, ketka kuuntelevat mitakin kanavaa.
 *  - kun joku subscribaa QBCC kanavalle, pyytaa QueueBroadcasteria castaamaan.
 *  HUOM: Spring hajoaa, jos kaytetaan Autowired konstruktoria tassa luokassa!
 */
@Component
public class SubscribeEventListener
        implements ApplicationListener<ApplicationEvent> {

    /**
     * Aika millisekunteina, joka odotetaan, ennen kuin suoritetaan Timer-
     * ajastimen määrittämän TimerTask-olion run-metodi.
     */
    private static final int TIMER_DELAY_MS = 10;

    /**
     * Kun jäsennetään Http-osoite String-taulukoksi handleSubscribe-metodissa,
     * tulee taulukosta ottaa alkio, joka on tämän muuttujan arvon mukaisessa
     * kohdassa taulukkoa.
     */
    private static final int POSITION_OF_ELEMENT_IN_HTTP_ADDRESS_ARRAY = 3;

    /** Session Repository. */
    @Autowired
    private SessionRepo sessionRepo;

    /** Queue Broadcaster. */
    @Autowired
    private QueueBroadcaster queueBroadcaster;

    /** Viestien lahetys. */
    @Autowired
    private SimpMessagingTemplate broker;

    /** Chat Logger (broadcastaa). */
    @Autowired
    private ChatLogger chatLogger;

    /** Mapper. */
    @Autowired
    private Mapper mapper;

    /** Kaynnistaa SessionSubscribeEventeista timerin handleSubscribe-metodiin.
     * @param event Kaikki applikaatioeventit aktivoivat taman metodin.
     */
    @Override
    public final void onApplicationEvent(
            final ApplicationEvent event
    ) {
        /** Ei kaynnisteta turhia timereita muista applikaatioeventeista. */
        if (event.getClass() != SessionSubscribeEvent.class) {
            return;
        }

        /** Eventin kasittelyn voi ajatella tapahtuvan kahdessa osassa:
         * 1. Spring kirjaa kanavan subscribaajiin uuden kuuntelijan ylos
         * 2. Logiikka handleSubscribe -metodissa
         *
         * Haluaisimme, etta 1) suoritetaan ennen 2).
         *
         * Valitettavasti tama EventListener aktivoituu kesken 1) suorituksen.
         * Spring ei tarjoa meille nakyvyytta siihen, milloin 1) on suoritettu
         * loppun.
         *
         * Jos kutsuisimme handleSubscribe -metodia suoraan tassa nyt,
         * kavisi usein niin ettei uusi kuuntelija saa mahdollisia
         * broadcasteja lainkaan, silla broadcastit lahetetaan kanavalle
         * ennen kuin Spring on ehtinyt kirjata uuden kuuntelijan mukaan.
         *
         * Timerin avulla saadaan 2) suoritettua eri threadissa kuin tassa,
         * siina toivossa etta threadi joka suorittaa 1) on ehtinyt suorittaa
         * subscriben kirjaamisen loppuun.
         *
         * Testattu: 1ms timer toimi lahes aina.
         * 10ms timerilla ei toistaiseksi havaittu samanaikaisuusvirheita. */
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleSubscribe((SessionSubscribeEvent) event);
            }
        }, TIMER_DELAY_MS);
    }

    /** Kasittelee subscribe -tapahtumat
     *      (sen jalkeen, kun Interceptor on validoinut ne).
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
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        String channelId = channelIdWithPath.split("/")
                [POSITION_OF_ELEMENT_IN_HTTP_ADDRESS_ARRAY];
        Channel channel = mapper.getChannel(channelId);
        channel.addSubscriber(session);

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

    /** Vaaditaan dependency injektion toimimiseen tassa tapauksessa.
     * @param repo repo
     */
    public synchronized void setSessionRepo(final SessionRepo repo) {
        this.sessionRepo = repo;
    }

    /** Vaaditaan dependency injektion toimimiseen tassa tapauksessa.
     *  @return SessionRepo sessionRepo
     * */
    public synchronized SessionRepo getSessionRepo() {
        return this.sessionRepo;
    }

    /**
     * Testausta helpottamaan.
     * @param qbc qbc
     */
    public synchronized void setQueueBroadcaster(
            final QueueBroadcaster qbc
    ) {
        this.queueBroadcaster = qbc;
    }

    /**
     * Testausta helpottamaan.
     * @param pBroker p
     */
    public synchronized void setBroker(
            final SimpMessagingTemplate pBroker
    ) {
        this.broker = pBroker;
    }

    /**
     * Testausta helpottamaan.
     * @param pChatLogger p
     */
    public synchronized void setChatLogger(
            final ChatLogger pChatLogger
    ) {
        this.chatLogger = pChatLogger;
    }

    /**
     * Testausta helpottamaan.
     * @param pMapper p
     */
    public synchronized void setMapper(
            final Mapper pMapper
    ) {
        this.mapper = pMapper;
    }
}
