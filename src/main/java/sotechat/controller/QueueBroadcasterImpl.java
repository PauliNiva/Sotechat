package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;

import sotechat.service.QueueService;
import static sotechat.config.StaticVariables.QUEUE_BROADCAST_CHANNEL;

/**
 * Tiedottaa jonon tilanteesta kaikille ammattilaisille
 * (QBCC kanavan tilanneille).
 */
@Component
public class QueueBroadcasterImpl implements QueueBroadcaster {

    /**
     * Jonon tilan kysymiseen.
     */
    private final QueueService queueService;

    /**
     * Viestien lahettamiseen.
     */
    private MessageBroker broker;

    /**
     * Edellisen tiedotuksen ajankohta.
     */
    private Long lastBroadcastTime;

    /**
     * Millisekunneissa minimiviive tiedotusten valilla.
     */
    public static final long QBC_DELAY_MS = 100L;

    /**
     * Konstruktori.
     *
     * @param pQueueService p
     * @param pMessageBroker p
     */
    @Autowired
    public QueueBroadcasterImpl(
            final QueueService pQueueService,
            final MessageBroker pMessageBroker
    ) {
        this.queueService = pQueueService;
        this.broker = pMessageBroker;
        this.lastBroadcastTime = 0L;
    }

    /**
     * Tiedottaa jonon tilanteen kaikille QBCC-kanavan tilanneille.
     * Tama metodi ohjaa tehtavan mahdollisen timerin
     * kautta metodille actuallyBroadcast.
     * <p>
     * Operaatio on suhteellisen raskas ja palvelinta voisi kyykyttaa
     * aiheuttamalla esim. tuhansia paivityksia sekunnissa.
     * Suojakeinona palvelunestohyokkayksia vastaan tiedotusten
     * valille on asetettu minimiviive.
     */
    public synchronized void broadcastQueue() {
        long timeNow = new DateTime().getMillis();
        if (lastBroadcastTime + QBC_DELAY_MS < timeNow) {
            /* Jos ei olla juuri asken tiedotettu, tehdaan se nyt. */
            actuallyBroadcast();
            lastBroadcastTime = new DateTime().getMillis();
        } else if (lastBroadcastTime < timeNow) {
            /* Jos ollaan askettain tiedotettu, halutaan
             * viivastyttaa seuraavaa tiedotusta, mutta ei
              * haluta kaynnistaa useita ajastimia. Sen vuoksi
              * lastBroadcastTime asetetaan tulevaisuuteen ja
              * else if -ehdossa tarkistetaan sen avulla, onko
              * uusi tiedotus jo ajastettu. */
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

    /**
     * Tiedottaa valittomasti jonon tilanteen kaikille
     * QBCC-kanavan tilaajille (hoitajille).
     */
    private synchronized void actuallyBroadcast() {
        String qbcc = "/toClient/" + QUEUE_BROADCAST_CHANNEL;
        String qAsJson = queueService.toString();
        broker.convertAndSend(qbcc, qAsJson);
    }

}
