package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sotechat.service.QueueService;
import sotechat.service.StateService;

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
