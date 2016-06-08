package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sotechat.service.QueueService;
import sotechat.service.StateService;

import java.util.Timer;
import java.util.TimerTask;

/** Tiedottaa jonon tilanteesta kaikille QBCC subscribanneille. */
@Component
public class QueueBroadcaster {

    /** Queue Service. */
    private final QueueService queueService;

    /** Taikoo viestien lahetyksen. */
    private SimpMessagingTemplate brokerMessagingTemplate;

    /** Konstruktori.
     * @param pQueueService queueService
     * @param pSimpMessagingTemplate broker
     */
    @Autowired
    public QueueBroadcaster(
            final QueueService pQueueService,
            final SimpMessagingTemplate pSimpMessagingTemplate
    ) {
        this.queueService = pQueueService;
        this.brokerMessagingTemplate = pSimpMessagingTemplate;
    }

    /** EI KAYTOSSA JUURI NYT.
     * Broadcasting every 1 second to fix subscribe+broadcast timing issues.
     * TODO: Properly for production.
     */
    public final void setBroadcastEvery1Second() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                broadcastQueue();
            }
        }, 1 * 1000, 1 * 1000);
    }

    /** Tiedottaa jonon tilanteen kaikille QBCC subscribaajille (hoitajille).
     * TODO: Protection against flooding (max 1 broadcast/second).
     */
    public final void broadcastQueue() {
        int delay = 50; // milliseconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                syncBcQ();
            }
        }, delay);

    }

    /**
     * Yritys korjata samanaikaisuusongelmia.
     * TODO: Refactor
     */
    public final synchronized void syncBcQ() {
        String qbcc = "/toClient/"
                + StateService.QUEUE_BROADCAST_CHANNEL;
        String qAsJson = queueService.toString();
        brokerMessagingTemplate.convertAndSend(qbcc, qAsJson);
    }

}
