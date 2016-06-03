package sotechat.data;

import javax.servlet.http.HttpSession;

public interface SessionRepo {
    void mapHttpSessionToSessionId(String sessionId, HttpSession session);
    HttpSession getHttpSession(String sessionId);
}
