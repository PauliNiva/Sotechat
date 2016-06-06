package sotechat.data;

import javax.servlet.http.HttpSession;
import java.security.Principal;

public interface SessionRepo {
    void mapHttpSessionToSessionId(String sessionId, HttpSession session);
    HttpSession getHttpSession(String sessionId);
    HttpSession getLatestHttpSession();
    void updateSessionAttributes(
            HttpSession session,
            Principal professional
    );
    void addChannel(HttpSession session, String channelId);
}
