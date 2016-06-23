package sotechat.util;

import sotechat.data.Session;

/**
 * Testien selkeyttamiseksi ja toiston vahentamiseksi luotu wrapperi.
 */
public class User {

    public MockPrincipal principal;
    public MockHttpServletRequest req;

    public Session session;
    public String sessionId;
    public String userId;

}
