package sotechat.connectionEvents;

import org.springframework.beans.factory.annotation.Autowired;
import sotechat.controller.QueueBroadcaster;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.service.QueueService;

import java.util.Timer;
import java.util.TimerTask;

public class ConnectionHandler {

    @Autowired
    private ConnectionRepo connectionRepo;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueBroadcaster queueBroadcaster;
  //  private String sessionId;
    private final int WAIT_TIME_BEFORE_SCANNING_USER_ACTIVITY = 2000;
    private final int WAIT_TIME_BEFORE_SCANNING_PRO_ACTIVITY = 2000;

    public ConnectionHandler() {
    }

    public void initiateWaitBeforeScanningForInactiveUsers(final String sessionId) {
        int delay = WAIT_TIME_BEFORE_SCANNING_USER_ACTIVITY; // milliseconds. TODO: test 1ms, pitaisi toimia.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeInactiveUsersFromQueue(sessionId);
            }
        }, delay);
    }

    public void initiateWaitBeforeScanningForInactiveProfessional(final String sessionId) {
        int delay = WAIT_TIME_BEFORE_SCANNING_PRO_ACTIVITY;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeProSession(sessionId);
            }
        }, delay);
    }

    public void removeInactiveUsersFromQueue(String sessionId) {
        if (!this.connectionRepo.getSessionConnectionStatus(sessionId)) {
            for (int i = 0; i < 10; i++) {
                System.out.println("Removing user from queue");
            }
            if (this.sessionRepo.getSessionObj(sessionId) != null) {
                Session session = this.sessionRepo.getSessionObj(sessionId);
                if (session.get("channelId") != null) {
                    String channelId = session.get("channelId");
                    this.sessionRepo.removeSession(sessionId);
                    this.queueService.removeFromQueue(channelId);
                    this.queueBroadcaster.broadcastQueue();
                }
            }
        }
    }

    public void removeProSession(String sessionId) {
        for (int i = 0; i < 10; i++) {
            System.out.println("Removing pro session");
        }
       // this.sessionRepo.removeSession(sessionId);
    }
}
