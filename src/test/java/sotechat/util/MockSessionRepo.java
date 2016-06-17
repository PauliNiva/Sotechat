package sotechat.util;

import sotechat.data.Session;
import sotechat.data.SessionRepo;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class MockSessionRepo implements SessionRepo {

    private Map<String, Session> sessionRepo;

    public MockSessionRepo() {
        this.sessionRepo = new HashMap();
    }

    @Override
    public Session getSessionObj(String sessionId) {
        return this.sessionRepo.get(sessionId);
    }

    @Override
    public Session updateSession(HttpServletRequest req, Principal professional) {
        return null;
    }

    @Override
    public void updateSessionAttributes(Session session, Principal professional) {
        this.sessionRepo.put(session.get("id"), session);
    }

}
