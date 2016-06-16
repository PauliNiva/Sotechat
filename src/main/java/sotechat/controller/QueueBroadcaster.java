package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sotechat.service.QueueService;

import java.util.Timer;
import java.util.TimerTask;
import static sotechat.config.StaticVariables.QUEUE_BROADCAST_CHANNEL;

/** Tiedottaa jonon tilanteesta kaikille QBCC subscribanneille. */
@Component
public class QueueBroadcaster {

    /** Queue Service. */
    private final QueueService queueService;

    /** Taikoo viestien lahetyksen. */
    private SimpMessagingTemplate broker;

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
        this.broker = pSimpMessagingTemplate;
    }

    /** Tiedottaa jonon tilanteen kaikille QBCC subscribaajille (hoitajille).
     * Timeria kaytetaan samanaikaisuusongelmien korjaamiseen.
     * TODO: Protection against flooding (max 1 broadcast/second).
     */
    public final void broadcastQueue() {
        int delay = 50; // milliseconds. TODO: test 1ms, pitaisi toimia.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                syncBcQ();
            }
        }, delay);

    }

    /** Timerin kutsuma broadcast.
     * TODO: Refactor
     */
    private synchronized void syncBcQ() {
        String qbcc = "/toClient/" + QUEUE_BROADCAST_CHANNEL;
        String qAsJson = queueService.toString();
        broker.convertAndSend(qbcc, qAsJson);
    }

}
