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
     *
     * @param pSimpMessagingTemplate sffsdsdf
     */
    @Autowired
    public QueueBroadcaster(
            final QueueService pQueueService,
            final SimpMessagingTemplate pSimpMessagingTemplate
    ) {
        this.queueService = pQueueService;
        this.brokerMessagingTemplate = pSimpMessagingTemplate;
        setBroadcastEvery1Second();
    }

    /**
     * Broadcasting every 1 second to fix subscribe+broadcast timing issues.
     * TODO: Properly for production.
     */
    public void setBroadcastEvery1Second() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                broadcastQueue();
            }
        }, 1*1000, 1*1000);
    }

    /**
     * TODO: Protection against flooding (max 1 broadcast/second).
     */
    public final void broadcastQueue() {
        String qbcc = "/toClient/" + StateService.QUEUE_BROADCAST_CHANNEL;
        String qAsJson = queueService.toString();
        brokerMessagingTemplate.convertAndSend(qbcc, qAsJson);
    }

}
