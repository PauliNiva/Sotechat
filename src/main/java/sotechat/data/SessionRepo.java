package sotechat.data;

import javax.servlet.http.HttpServletRequest;
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

    /** Paivittaa tarpeen vaatiessa
     *  - sessio-objekti mappaykset
     *  - sessio-attribuutit (kutsumalla metodia updateSessionAttributes)
     * @param req taalta sessioId
     * @param professional taalta kirjautumistiedot
     * @return sessio-olio
     */
    Session updateSession(
            HttpServletRequest req,
            Principal professional
    );

    /** Paivittaa tarpeen vaatiessa sessio-attribuutit.
     * @param session sessio
     * @param professional kirjautumistiedot
     */
    void updateSessionAttributes(
            final Session session,
            final Principal professional
    );

    void removeSession(final String sessionId);

    public void leaveChannel(
            String channelId,
            String sessionId
    );
}
