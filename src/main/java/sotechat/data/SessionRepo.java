package sotechat.data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

/** Session Repository auttaa sessioiden kasittelyssa.
 */
public interface SessionRepo {

    /** Hakee sessio-olion sessioId:n perusteella.
     * @param sessionId sessioId
     * @return sessio-olio
     */
    Session getSessionObj(
            String sessionId
    );

    /** Paivittaa tarpeen vaatiessa session attribuutteja.
     * @param req taalta sessioId
     * @param professional taalta kirjautumistiedot
     * @return sessio-olio
     */
    Session updateSession(
            HttpServletRequest req,
            Principal professional
    );

    void removeSession(String sessionId);
}
