package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sotechat.service.StateService;

/** Tiedottaa jonon tilanteesta kaikille QBCC subscribanneille. */
@Component
public class QueueBroadcaster {

    /** State Service. */
    private final StateService stateService;

    /** Taikoo viestien lahetyksen. */
    private SimpMessagingTemplate brokerMessagingTemplate;

    /** Konstruktori.
     *
     * @param pStateService sffdsdf
     * @param pSimpMessagingTemplate sffsdsdf
     */
    @Autowired
    public QueueBroadcaster(
            final StateService pStateService,
            final SimpMessagingTemplate pSimpMessagingTemplate
    ) {
        this.stateService = pStateService;
        this.brokerMessagingTemplate = pSimpMessagingTemplate;
    }


    /**
     * TODO: Protection against flooding (max 1 broadcast/second).
     */
    public final void broadcastQueue() {
        String qbcc = "/toClient/" + StateService.QUEUE_BROADCAST_CHANNEL;
        String qAsJson = stateService.getQueueAsJson();
        brokerMessagingTemplate.convertAndSend(qbcc, qAsJson);
    }

}
