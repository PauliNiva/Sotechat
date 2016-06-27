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

    /** Lukumaara kirjautuneita ammattilaisia, jotka ovat valmiita
     * vastaanottamaan uusia asiakkaita. Liittyy chatin sulkemiseen. */
    private int countOfProsAcceptingNewCustomers;

    /** Mapper. */
    private final Mapper mapper;

    /** Konstruktori.
     * @param pMapper mapperi.
     */
    @Autowired
    public SessionRepo(
            final Mapper pMapper
    ) {
        this.mapper = pMapper;
        initialize();
    }

    /** Alustaminen, jota kutsutaan seka olion
     * luonnissa etta sessioiden unohtamisessa. */
    private void initialize() {
        this.sessionsBySessionId = new HashMap<>();
        this.sessionsByUserId = new HashMap<>();
        this.proUserSessions = new HashMap<>();
        this.countOfProsAcceptingNewCustomers = 0;
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
    public synchronized void leaveChannel(
            final String channelId,
            final String sessionId
    ) {
        Session session = getSessionFromSessionId(sessionId);
        session.removeChannel(channelId);
        updateCountOfProsAcceptingNewCustomers();
        Channel channel = mapper.getChannel(channelId);
        channel.setActive(false);
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
    public synchronized Session updateSession(
            final HttpServletRequest req,
            final Principal professional
    ) {
        String sessionId = req.getSession().getId();

        Session session = updateSessionObjectMapping(sessionId, professional);
        updateSessionAttributes(session, professional);
        session.set("sessionId", sessionId);
        updateCountOfProsAcceptingNewCustomers();

        return session;
    }

    /** Paivittaa tarpeen vaatiessa session-olion attribuutteja.
     * @param session session-olio
     * @param professional kirjautumistiedot, saa olla null
     */
    public void updateSessionAttributes(
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
            session.set("category", "Aihe ei tiedossa");
            Channel channel = mapper.createChannel();
            channel.allowParticipation(session);
        }

        if (chatClosed() && session.get("state").equals("start")) {
            /** Ei nayteta alkunakymaa asiakkaille, jos chat on suljettu. */
            session.set("state", "closed");
        } else if (!chatClosed() && session.get("state").equals("closed")) {
            /** Chat oli joskus suljettu, mutta nyt se on avattu. */
            session.set("state", "start");
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
            session.set("online", "true");
        }

        /** Muistetaan jatkossakin, mihin sessioon tama sessionId liittyy. */
        sessionsBySessionId.put(sessionId, session);

        /** Jos kyseessa pro, muistetaan etta proUsername liittyy sessioon. */
        if (professional != null) {
            proUserSessions.put(professional.getName(), session);
        }

        return session;
    }

    /** Asettaa ammattilaisen online-statukseksi "true" tai "false".
     * Oletettavasti pyynto on validoitu ennen taman metodin kutsua.
     * @param req sessioId taalta
     * @param onlineStatus asettava onlineStatus
     */
    public synchronized void setOnlineStatus(
            final HttpServletRequest req,
            final String onlineStatus
    ) {
        String sessionId = req.getSession().getId();
        Session session = sessionsBySessionId.get(sessionId);
        session.set("online", onlineStatus);
        updateCountOfProsAcceptingNewCustomers();
    }

    /** Paivittaa muistiin lukumaaran kirjautuneista ammattilaisista,
     * jotka hyvaksyvat uusia asiakkaita. Jos kirjautuneita ammattilaisia
     * voi olla yli 1000, metodi olisi hyva kirjoittaa tehokkaammin. */
    private synchronized void updateCountOfProsAcceptingNewCustomers() {
        countOfProsAcceptingNewCustomers = 0;
        for (Session session : proUserSessions.values()) {
            if (session.get("online").equals("true")) {
                countOfProsAcceptingNewCustomers++;
            }
        }
    }

    /** Onko chat suljettu? Tarkoitettu kaytettavaksi siihen liittyen,
     * hyvaksytaanko uusia asiakkaita enaa jonoon (tai edes aloitussivulle).
     * Vanhat asiakkaat on tarkoitus kasitella, vaikka chat olisikin "suljettu".
     * @return true jos uusia asiakkaita ei hyvaksyta.
     */
    public synchronized boolean chatClosed() {
        return countOfProsAcceptingNewCustomers == 0;
    }

    /** Testausta helpottamaan metodi sessioiden unohtamiseen. */
    public void forgetAllSessions() {
        initialize();
    }
}
