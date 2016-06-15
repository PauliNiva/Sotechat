package sotechat.connectionEvents;

import org.springframework.scheduling.annotation.Scheduled;
import sotechat.controller.QueueBroadcaster;
import sotechat.data.Queue;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import javax.servlet.http.HttpSession;

public class ConnectionHandler {

    private ConnectionRepo connectionRepo;
    private SessionRepo sessionRepo;
    private Queue queue;
    private QueueBroadcaster queueBroadcaster;
    private String sessionId;
    private final int WAIT_TIME_BEFORE_METHOD_INVOCATION = 10000;

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
        for (int i = 0; i < 10; i++) {
            System.out.println("wololooo");
        }
        if (this.sessionId != null) {
            if (!this.connectionRepo.getSessionConnectionStatus(sessionId)) {
                Session session = null;
                if (this.sessionRepo.getSessionObj(sessionId) != null) {
                    session = this.sessionRepo.getSessionObj(sessionId);
                    String channelId = null;
                    if (session.get("channelId") != null) {
                        channelId = session.get("channelId");
                        this.sessionRepo.removeSession(this.sessionId);
                        this.queue.remove(channelId);
                        this.queueBroadcaster.broadcastQueue();
                    }
                }
            }
        }
    }

    public void setSessionId(String id) {
        this.sessionId = id;
    }
}
