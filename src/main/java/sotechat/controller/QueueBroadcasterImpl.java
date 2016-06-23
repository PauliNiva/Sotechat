package sotechat.controller;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sotechat.service.QueueService;

import java.util.Timer;
import java.util.TimerTask;

import static sotechat.config.StaticVariables.QUEUE_BROADCAST_CHANNEL;

/** Tiedottaa jonon tilanteesta kaikille ammattilaisille
 * (QBCC kanavalle subscribanneille). */
@Component
public class QueueBroadcasterImpl implements QueueBroadcaster {

    /** Jonon tila kysytaan taalta. */
    private final QueueService queueService;

    /** Viestien lahetys. */
    private SimpMessagingTemplate broker;

    /** Ajanhetki edelliselle broadcastille. */
    private Long lastBroadcastTime;

    /** Millisekunneissa minimiviive broadcastien valilla. */
    public static final long QBC_DELAY_MS = 100L;

    /** Konstruktori.
     * @param pQueueService queueService
     * @param pSimpMessagingTemplate broker */
    @Autowired
    public QueueBroadcasterImpl(
            final QueueService pQueueService,
            final SimpMessagingTemplate pSimpMessagingTemplate
    ) {
        this.queueService = pQueueService;
        this.broker = pSimpMessagingTemplate;
        this.lastBroadcastTime = 0L;
    }

    /**
     * Tiedottaa jonon tilanteen kaikille QBCC subscribaajille.
     * Operaatio on suhteellisen raskas ja palvelinta voisi kyykyttaa
     * aiheuttamalla esim. tuhansia paivityksia sekunnissa.
     * Suojakeinona palvelunestohyokkayksia vastaan tiedotusten
     * valille on asetettu minimiviive. Tama metodi ohjaa tehtavan
     * mahdollisen timerin kautta metodille actuallyBroadcast.
     */
    public synchronized void broadcastQueue() {
        long timeNow = new DateTime().getMillis();
        if (lastBroadcastTime + QBC_DELAY_MS < timeNow) {
            /* Jos ei olla juuri asken broadcastattu, tehdaan se nyt. */
            actuallyBroadcast();
            lastBroadcastTime = new DateTime().getMillis();
        } else if (lastBroadcastTime < timeNow) {
            /* Jos ollaan askettain broadcastattu, halutaan
             * viivastyttaa seuraavaa broadcastia, mutta ei
              * haluta kaynnistaa useita timereita. Sen vuoksi
              * lastBroadcastTime asetetaan tulevaisuuteen ja
              * else if -ehdossa tarkistetaan sen avulla, onko
              * uusi broadcast jo skeduloitu. */
            lastBroadcastTime += QBC_DELAY_MS;
            long delayToNext = lastBroadcastTime - timeNow;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    actuallyBroadcast();
                }
            }, delayToNext);
        }
    }

    /** Tiedottaa valittomasti jonon tilanteen kaikille
     * QBCC subscribaajille (hoitajille). */
    private synchronized void actuallyBroadcast() {
        String qbcc = "/toClient/" + QUEUE_BROADCAST_CHANNEL;
        String qAsJson = queueService.toString();
        broker.convertAndSend(qbcc, qAsJson);
    }

}
