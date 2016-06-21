package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sotechat.service.QueueService;

import static sotechat.config.StaticVariables.QUEUE_BROADCAST_CHANNEL;

/** Tiedottaa jonon tilanteesta kaikille QBCC subscribanneille. */
@Component
public class QueueBroadcasterImpl implements QueueBroadcaster {

    /** Queue Service. */
    private final QueueService queueService;

    /** Taikoo viestien lahetyksen. */
    private SimpMessagingTemplate broker;

    /** Konstruktori.
     * @param pQueueService queueService
     * @param pSimpMessagingTemplate broker
     */
    @Autowired
    public QueueBroadcasterImpl(
            final QueueService pQueueService,
            final SimpMessagingTemplate pSimpMessagingTemplate
    ) {
        this.queueService = pQueueService;
        this.broker = pSimpMessagingTemplate;
    }

    /** Tiedottaa jonon tilanteen kaikille QBCC subscribaajille (hoitajille).
     * TODO: Protection against flooding (max 1 broadcast/second).
     */
    public final void broadcastQueue() {
        String qbcc = "/toClient/" + QUEUE_BROADCAST_CHANNEL;
        String qAsJson = queueService.toString();
        broker.convertAndSend(qbcc, qAsJson);
    }

}
