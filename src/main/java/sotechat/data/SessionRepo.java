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
public class SessionRepo extends MapSessionRepository {

    /** Avain sessio-ID, arvo Sessio-olio.
     * HUOM: Usea sessio-ID voi viitata samaan Session-olioon! */
    private HashMap<String, Session> sessionsBySessionId;

    /** Avain userId, arvo Sessio-olio. */
    private HashMap<String, Session> sessionsByUserId;

    /** Avain proUsername, arvo Sessio-olio.
     * Tehty toteuttamaan hoitajan kayttotapaus: logout->login->jatka chatteja*/
    private HashMap<String, Session> proUserSessions;

    /** Mapper. */
    private final Mapper mapper;

    /** Konstruktori.
     * @param pMapper mapperi.
     */
    @Autowired
    public SessionRepo(
            final Mapper pMapper
    ) {
        this.sessionsBySessionId = new HashMap<>();
        this.sessionsByUserId = new HashMap<>();
        this.proUserSessions = new HashMap<>();
        this.mapper = pMapper;
    }

    /** Kaivaa sessionId:lla session-olion.
     * @param sessionId sessionId
     * @return sesson-olio
     */
    public synchronized Session getSessionFromSessionId(
            final String sessionId
    ) {
        return sessionsBySessionId.get(sessionId);
    }

    public final synchronized Session getSessionFromUserId(
            final String userId
    ) {
        return sessionsByUserId.get(userId);
    }

    /** Kanavalta poistuminen. Kutsutaan tapauksissa:
     * - Kun keskustelija painaa nappia "Poistu"
     * - Kun keskustelija on kadonnut eika tule takaisin pian (timeout)
     * @param channelId p
     * @param sessionId p
     */
    public final synchronized void leaveChannel(
            final String channelId,
            final String sessionId
    ) {
        Session session = getSessionFromSessionId(sessionId);
        session.removeChannel(channelId);
        Channel channel = mapper.getChannel(channelId);
        for (String someUserId : channel.getActiveUserIds()) {
            Session someSession = getSessionFromUserId(someUserId);
            String someSessionId = someSession.get("sessionId");
            if (!someSession.isPro()) {
                /** Jos kukaan lahtee kanavalta, jolla on normikayttajia,
                 * unohdetaan normikayttajien sessiot. */
                sessionsByUserId.remove(someUserId);
                sessionsBySessionId.remove(someSessionId);
            }
        }
        channel.removeSubscriber(session);
        channel.removeActiveUserId(session.get("userId"));
    }

    /** Paivittaa tarpeen vaatiessa sessioniin liittyvia tietoja.
     *      - Paivittaa mappayksia "sessioId liittyy tahan sessio-olioon" yms.
     *      - Paivittaa sessio-olion attribuutteja
     * @param req taalta saadaan Http Session Id
     * @param professional taalta saadaan kirjautumistiedot, voi olla null
     * @return Session-olio
     */
    public final synchronized Session updateSession(
            final HttpServletRequest req,
            final Principal professional
    ) {
        String sessionId = req.getSession().getId();

        /** Paivityslogiikka jaettu kahteen metodiin, alla kutsut. */
        Session session = updateSessionObjectMapping(sessionId, professional);
        updateSessionAttributes(session, professional);
        session.set("sessionId", sessionId);

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

        if (professional != null) {
            /* Jos client on autentikoitunut ammattilaiseksi */
            username = professional.getName();
            userId = mapper.getIdFromRegisteredName(username);
            session.set("state", "pro");
            session.set("username", username);
            session.set("userId", userId);
            session.updateChannelsAttribute();
        } else if (username.isEmpty()) {
            /* Uusi kayttaja */
            username = "Anon";
            userId = mapper.generateNewId();
            session.set("username", username);
            session.set("userId", userId);
            session.set("state", "start");
            session.set("category", "Kategoria"); //TODO
            Channel channel = mapper.createChannel();
            channel.allowParticipation(session);
        }

        /** Muistetaan jatkossakin, mihin sessioon tama userId liittyy. */
        sessionsByUserId.put(userId, session);
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

        /** Muistetaan jatkossakin, mihin sessioon tama sessionId liittyy. */
        sessionsBySessionId.put(sessionId, session);

        /** Jos kyseessa pro, muistetaan etta proUsername liittyy sessioon. */
        if (professional != null) {
            proUserSessions.put(professional.getName(), session);
        }

        return session;
    }




    /** Testausta helpottamaan metodi sessioiden unohtamiseen. */
    public final void forgetSessions() {
        this.sessionsBySessionId.clear();
        this.sessionsByUserId.clear();
        this.proUserSessions.clear();
    }
}
