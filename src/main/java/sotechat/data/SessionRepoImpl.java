package sotechat.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;

/** Hoitaa Session-olioihin liittyvan kasittelyn.
 * esim. paivittaa session-attribuutteihin nimimerkin.
 */
@Component
public class SessionRepoImpl extends MapSessionRepository
        implements SessionRepo {

    /** Avain sessio-ID, arvo Sessio-olio.
     * HUOM: Usea sessio-ID voi viitata samaan Session-olioon! */
    private HashMap<String, Session> sessionsBySessionId;

    /** Avain userId, arvo Sessio-olio. */
    private HashMap<String, Session> sessionsByUserId;

    /** Avain proUsername, arvo Sessio-olio.
     * Tehty toteuttamaan hoitajan kayttotapaus: logout->login->jatka chatteja*/
    private HashMap<String, Session> proUserSessions;

    /** Mapperilta voi esim. kysya "mika username on ID:lla x?". */
    private final Mapper mapperService;

    /** Konstruktori.
     * @param pMapper mapperi.
     */
    @Autowired
    public SessionRepoImpl(
            final Mapper pMapper
    ) {
        this.sessionsBySessionId = new HashMap<>();
        this.proUserSessions = new HashMap<>();
        this.mapperService = pMapper;
    }

    /** Kaivaa sessionId:lla session-olion.
     * @param sessionId sessionId
     * @return sesson-olio
     */
    @Override
    public final synchronized Session getSessionObj(
            final String sessionId
    ) {
        return sessionsBySessionId.get(sessionId);
    }

    /** Paivittaa tarpeen vaatiessa sessioniin liittyvia tietoja.
     *      - Paivittaa mappayksia "sessioId liittyy tahan sessio-olioon" yms.
     *      - Paivittaa sessio-olion attribuutteja
     * @param req taalta saadaan Http Session Id
     * @param professional taalta saadaan kirjautumistiedot, voi olla null
     * @return Session-olio
     */

    /** Merkitaan, etta sessioId on poistunut kanavalta channelId.
     * @param channelId
     * @param sessionId
     */
    @Override
    public final synchronized void leaveChannel(
            String channelId,
            String sessionId
    ) {
        Session session = getSessionObj(sessionId);
        session.removeChannel(channelId);
        String channelIdWithPath = "/toClient/chat/" + channelId;
        mapperService.removeSessionFromChannel(channelIdWithPath, session);
    }

    @Override
    public final synchronized Session updateSession(
            final HttpServletRequest req,
            final Principal professional
    ) {
        String sessionId = req.getSession().getId();

        /** Paivityslogiikka jaettu kahteen metodiin, alla kutsut. */
        Session session = updateSessionObjectMapping(sessionId, professional);
        updateSessionAttributes(session, professional);

        /** Kirjataan tietoja myos mapperiin, monesti aiemman paalle. */
        mapperService.mapUsernameToId(
                session.get("userId"), session.get("username"));

        return session;
    }

    /** Paivittaa tarpeen vaatiessa session-olion attribuutteja.
     * @param session session-olio
     * @param professional kirjautumistiedot, saa olla null
     */
    public final void updateSessionAttributes(
            final Session session,
            final Principal professional
    ) {

        /** Kaivetaan username ja id sessio-attribuuteista. */
        String username = session.get("username");
        String userId = session.get("userId");

        /** Paivitetaan muuttujat, jos tarpeellista. */
        if (professional != null) {
            /* Jos client on autentikoitunut ammattilaiseksi */
            username = professional.getName();
            userId = mapperService.getIdFromRegisteredName(username);
            session.set("state", "pro");
            session.updateChannelsAttribute();
        } else if (username.isEmpty()) {
            /* Uusi kayttaja */
            username = "Anon";
            userId = mapperService.generateNewId();
            session.set("state", "start");
            session.set("category", "Kategoria"); //TODO
            String randomNewChannel = mapperService.generateNewId();
            session.set("channelId", randomNewChannel);
            System.out.println("   luodaan uusi " + randomNewChannel);
        }

        /** Liitetaan muuttujien tieto sessioon (monesti aiemman paalle). */
        session.set("username", username);
        session.set("userId", userId);
    }

    /** Paivittaa mappayksia kuten "sessioId liittyy tahan sessio-olioon".
     * @param sessionId sessioId
     * @param professional autentikaatiotiedot, voi olla null
     * @return sessio-olio
     */
    private Session updateSessionObjectMapping(
            final String sessionId,
            final Principal professional
    ) {
        Session session = sessionsBySessionId.get(sessionId);
        if (session != null) {
            /** Talle sessioId:lle on jo mapatty Sessio-olio, palautetaan se. */
            return session;
        }

        if (professional != null) {
            /** Onko hoitajalla olemassaoleva vanha sessio? */
            String proUsername = professional.getName();
            session = proUserSessions.get(proUsername);
        }
        if (session == null) {
            /** Sessio edelleen tuntematon, luodaan uusi sessio. */
            session = new Session();
        }

        /** Muistetaan jatkossakin, etta tama sessionId liittyy sessioon. */
        sessionsBySessionId.put(sessionId, session);

        /** Jos kyseessa pro, muistetaan etta proUsername liittyy sessioon. */
        if (professional != null) {
            proUserSessions.put(professional.getName(), session);
        }

        return session;
    }
}
