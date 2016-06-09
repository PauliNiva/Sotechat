package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sotechat.data.ChatLogger;
import sotechat.wrappers.MsgToClient;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/** Broadcastaa chatin logit, kun uusi jasen liittyy kanavalle (esim. F5).
 */
@Component
public class ChatLogBroadcaster {

    /** ChatLogger. */
    private final ChatLogger chatLogger;

    /** Taikoo viestien lahetyksen. */
    private SimpMessagingTemplate broker;

    /** Konstruktori.
     * @param pChatLogger chatLogger
     * @param broker broker
     */
    @Autowired
    public ChatLogBroadcaster(
            final ChatLogger pChatLogger,
            final SimpMessagingTemplate broker
    ) {
        this.chatLogger = pChatLogger;
        this.broker = broker;
    }


    /** Metodi lahettaa kanavan chat-logit kanavan subscribaajille.
     * Ennen lokeja lahetetaan erikoisviesti $CLEAR$ = tyhjenna ruutu.
     * TODO: Broadcastaa vain sita pyytavalle tyypille, ei koko kanavalle.
     * TODO: Refactor.
     * TODO: Protection against flooding (max 1 broadcast/second).
     * @param channelId channelId
     */
    public final void broadcast(
            final String channelId
    ) {
        int delay = 200; // milliseconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                syncBroadcast(channelId);
            }
        }, delay);

    }

    /** Yritys korjata samanaikaisuusongelmia.
     * TODO: Refactor
     * @param channelId channelId
     */
    public final synchronized void syncBroadcast(
            final String channelId
    ) {
        System.out.println("TRYING TO BROADCAST channelId " + channelId);
        String channelIdWithPath = "/toClient/chat/" + channelId;
        List<MsgToClient> logs = chatLogger.getLogs(channelId);
        broker.convertAndSend(channelIdWithPath, "$CLEAR$");
        for (MsgToClient msg : logs) {
            System.out.println("    broadcasting " + msg.getContent());
            broker.convertAndSend(channelIdWithPath, msg);
        }
    }

}
