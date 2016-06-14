package sotechat.connectionEvents;

import org.springframework.scheduling.annotation.Scheduled;
import sotechat.controller.QueueBroadcaster;
import sotechat.data.Queue;
import sotechat.data.SessionRepo;

import javax.servlet.http.HttpSession;

public class ConnectionHandler {

    private ConnectionRepo connectionRepo;
    private SessionRepo sessionRepo;
    private Queue queue;
    private QueueBroadcaster queueBroadcaster;
    private String sessionId;
    private final int WAIT_TIME_BEFORE_METHOD_INVOCATION = 5000;

    public ConnectionHandler(ConnectionRepo pConnectionRepo,
                             SessionRepo pSessionRepo, Queue pQueue,
                             QueueBroadcaster pQueueBroadcaster) {
        this.connectionRepo = pConnectionRepo;
        this.sessionRepo = pSessionRepo;
        this.queue = pQueue;
        this.queueBroadcaster = pQueueBroadcaster;
    }

    @Scheduled(fixedRate = WAIT_TIME_BEFORE_METHOD_INVOCATION)
    public void removeInactiveUsersFromQueue() {
        if (this.sessionId != null) {
            if (!this.connectionRepo.getSessionConnectionStatus(sessionId)) {
                HttpSession session = this.sessionRepo
                        .getHttpSession(sessionId);
                String channelId = session.getAttribute("channelId").toString();
                if (channelId != null) {
                    this.sessionRepo.
                    this.queue.remove(channelId);
                    this.queueBroadcaster.broadcastQueue();
                }
            }
        }
    }

    public void setSessionId(String id) {
        this.sessionId = id;
    }
}
