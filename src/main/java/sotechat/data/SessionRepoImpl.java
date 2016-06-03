package sotechat.data;

import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Component
public class SessionRepoImpl extends MapSessionRepository
    implements SessionRepo {

    private HashMap<String, HttpSession> httpSessions;

    public SessionRepoImpl() {
        super();
        this.httpSessions = new HashMap();
    }

    @Override
    public void
    mapHttpSessionToSessionId(String sessionId, HttpSession session) {
        this.httpSessions.put(sessionId, session);
    }

    @Override
    public HttpSession getHttpSession(String sessionId) {
        return httpSessions.get(sessionId);
    }
}
