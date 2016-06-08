package sotechat.data;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/** Session Repository auttaa sessioiden kasittelyssa.
 */
public interface SessionRepo {

    /** Muistaa, mika sessio-olio liittyy mihinkin sessioId:hen.
     * @param sessionId sessioId
     * @param session sessio-olio
     */
    void mapHttpSessionToSessionId(
            String sessionId,
            HttpSession session
    );

    /** Hakee sessio-olion sessioId:n perusteella.
     * @param sessionId sessioId
     * @return sessio-olio
     */
    HttpSession getHttpSession(
            String sessionId
    );

    /** Hakee viimeisimman session. Lahinna avuksi testaukseen.
     * @return viimeisin sessio.
     */
    HttpSession getLatestHttpSession();
    void updateSessionAttributes(
            HttpSession session,
            Principal professional
    );

    /** Kirjaa ylos, etta session kayttaja on myos kanavalla kanavaId.
     * @param session sessio-olio
     * @param channelId lisattava kanavaId
     */
    void addChannel(
            HttpSession session,
            String channelId
    );

    /** Kirjaa ylos, etta session kayttaja ei ole enaa kanavalla kanavaId.
     * @param session sessio-olio
     * @param channelId poistettava kanavaId
     */
    void removeChannel(
            HttpSession session,
            String channelId
    );
}
