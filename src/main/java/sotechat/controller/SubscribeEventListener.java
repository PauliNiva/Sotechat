package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.data.Channel;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Kuuntelee WebSocket subscribe/unsubscribe -tapahtumia
 *  - pitaa kirjaa, ketka kuuntelevat mitakin polkua.
 *  - kun joku subscribaa QBCC polkuun, pyytaa QueueBroadcasteria tiedottamaan.
 *  HUOM: Spring hajoaa, jos kaytetaan Autowired konstruktoria tassa luokassa!
 */
@Component
public class SubscribeEventListener
        implements ApplicationListener<ApplicationEvent> {

    /**
     * Session Repository.
     */
    @Autowired
    private SessionRepo sessionRepo;

    /**
     * Queue Broadcaster.
     */
    @Autowired
    private QueueBroadcaster queueBroadcaster;

    /**
     * Viestien lahetys.
     */
    @Autowired
    private MessageBroker broker;

    /**
     * Viestien muistaminen.
     */
    @Autowired
    private ChatLogger chatLogger;

    /**
     * Mapper.
     */
    @Autowired
    private Mapper mapper;

    /**
     * Kaynnistaa SessionSubscribeEventeista timerin handleSubscribe-metodiin.
     *
     * @param event Kaikki applikaatioeventit aktivoivat taman metodin.
     */
    @Override
    public final void onApplicationEvent(final ApplicationEvent event) {
        /* Ei kaynnisteta turhia timereita muista applikaatioeventeista. */
        if (event.getClass() != SessionSubscribeEvent.class) {
            return;
        }

        /* Eventin kasittelyn voi ajatella tapahtuvan kahdessa osassa:
         * 1. Spring kirjaa kanavan subscribaajiin uuden kuuntelijan ylos
         * 2. Logiikka handleSubscribe -metodissa
         *
         * Haluaisimme, etta 1) suoritetaan ennen 2).
         *
         * Valitettavasti tama EventListener aktivoituu kesken 1) suorituksen.
         * Spring ei tarjoa meille nakyvyytta siihen, milloin 1) on suoritettu
         * loppun.
         *
         * Jos kutsuisimme handleSubscribe -metodia suoraan nyt,
         * kavisi usein niin ettei uusi kuuntelija saa mahdollisia
         * broadcasteja lainkaan, silla broadcastit lahetetaan kanavalle
         * ennen kuin Spring on ehtinyt kirjata uuden kuuntelijan mukaan.
         *
         * Timerin avulla saadaan 2) suoritettua eri threadissa kuin tassa,
         * siina toivossa etta threadi joka suorittaa 1) on ehtinyt suorittaa
         * subscriben kirjaamisen loppuun.
         *
         * Testattu: 1ms timer toimi lahes aina.
         * 10ms timerilla ei toistaiseksi havaittu samanaikaisuusvirheita.
         */
        int timerDelayMS = 10;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleSubscribe((SessionSubscribeEvent) event);
            }
        }, timerDelayMS);
    }

    /**
     * Kasittelee tilaus-tapahtumat
     * (sen jalkeen, kun Interceptor on validoinut ne).
     *
     * @param event Event.
     */
    private synchronized void handleSubscribe(
            final SessionSubscribeEvent event
    ) {
        MessageHeaders headers = event.getMessage().getHeaders();

        /* Interceptor estaa tilaukset, joista puuttuu sessionId.
         * Siksi allaoleva ei voi heittaa nullpointteria. */
        String sessionId = SimpMessageHeaderAccessor
                .getSessionAttributes(headers)
                .get("SPRING.SESSION.ID").toString();

        String channelIdWithPath = SimpMessageHeaderAccessor
                .getDestination(headers);

        /* Jos tilattu QBCC-polku, tiedotetaan jonon tilanne. */
        if (channelIdWithPath.equals("/toClient/QBCC")) {
            queueBroadcaster.broadcastQueue();
            return;
        }

        /* Lisataan Sessio kanavan aktiivisten WebSocket-yhteyksien settiin.
         * HUOM: Aktivoituu seka /queue/ etta /chat/ tilauksista! */
        Session session = sessionRepo.getSessionFromSessionId(sessionId);

        /* Polku on muotoa /toClient/chat/id. Kaivetaan sielta pelkka id. */
        String channelId = channelIdWithPath.split("/")[3];

        /* Haetaan Channel-olio channelId:n avulla. */
        Channel channel = mapper.getChannel(channelId);
        channel.addSubscriber(session);

        /* Jos tilattu /toClient/chat/{kanavaId} */
        String chatPrefix = "/toClient/chat/";
        if (channelIdWithPath.startsWith(chatPrefix)) {
            /* Lahetetaan kanavan chat-historia kaikille subscribaajille. */
            chatLogger.broadcast(channelId, broker);
            /* Ei laheteta tassa tietoa "uusi keskustelija liittynyt kanavalle"
             * vaan lahetetaan se WebSocketConnectHandlerissa. */
            if (!channel.isActive()) {
                /* Suljetun kanavan tilaus voi tapahtua esimerkiksi, kun
                 * ammattilaiskayttaja paivittaa sivun ja jotkin valilehdet
                  * sisaltavat suljettuja kanavia. */
                broker.sendClosedChannelNotice(channelId);
            }
        }
    }

    /**
     * Vaaditaan dependency injektion toimimiseen tassa tapauksessa.
     * @param repo repo
     */
    public synchronized void setSessionRepo(final SessionRepo repo) {
        this.sessionRepo = repo;
    }

    /**
     * Vaaditaan dependency injektion toimimiseen tassa tapauksessa.
     *
     *  @return SessionRepo sessionRepo
     * */
    public synchronized SessionRepo getSessionRepo() {
        return this.sessionRepo;
    }

    /**
     * Testausta helpottamaan.
     *
     * @param qbc qbc
     */
    public synchronized void setQueueBroadcaster(final QueueBroadcaster qbc) {
        this.queueBroadcaster = qbc;
    }

    /**
     * Testausta helpottamaan.
     *
     * @param pBroker p
     */
    public synchronized void setBroker(final MessageBroker pBroker) {
        this.broker = pBroker;
    }

    /**
     * Testausta helpottamaan.
     *
     * @param pChatLogger p
     */
    public synchronized void setChatLogger(final ChatLogger pChatLogger) {
        this.chatLogger = pChatLogger;
    }

    /**
     * Testausta helpottamaan.
     *
     * @param pMapper p
     */
    public synchronized void setMapper(final Mapper pMapper) {
        this.mapper = pMapper;
    }
}
