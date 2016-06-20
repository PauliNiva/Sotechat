package sotechat.connectionEvents;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

public class ConnectionRepo {

    private Map<String, Boolean> connectedSessions;

    public ConnectionRepo() {
        this.connectedSessions = new HashMap<>();
    }

    public void setSessionStatusToConnected(String id) {
        this.connectedSessions.put(id, true);
    }

    public void setSessionStatusToDisconnected(String id) {
        this.connectedSessions.put(id, false);
    }

    public boolean getSessionConnectionStatus(String id) {
        return this.connectedSessions.get(id);
    }
}
