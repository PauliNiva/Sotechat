package sotechat.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static sotechat.util.Utils.get;

/** Hoitaa Session-olioihi liittyvan kasittelyn.
 * esim. paivittaa session-attribuutteihin nimimerkin.
 */
@Component
public class SessionRepoImpl extends MapSessionRepository
    implements SessionRepo {

    /** Avain sessio-ID, arvo HttpSession-olio. */
    private HashMap<String, HttpSession> httpSessions;

    /** Avain = pro kayttajan sessio ID.
     *  Arvo = Setti kanavia, joilla kayttaja on. */
    private HashMap<String, HashSet<String>> proChannels;

    /** Kaytetaan testauksessa. */
    private HttpSession latestSession;

    /** Mapperilta voi esim. kysya "mika username on ID:lla x?". */
    private final Mapper mapperService;

    /** Konstruktori.
     * @param pMapper mapperi.
     */
    @Autowired
    public SessionRepoImpl(
            final Mapper pMapper
    ) {
        super();
        this.mapperService = pMapper;
        this.httpSessions = new HashMap<>();
        this.proChannels = new HashMap<>();
    }

    /** Jotta voidaan kaivaa sessionId:lla session-olio.
     * @param sessionId sessionID
     * @param session session-olio
     */
    @Override
    public final synchronized void mapHttpSessionToSessionId(
            final String sessionId,
            final HttpSession session
    ) {
        this.httpSessions.put(sessionId, session);
        this.latestSession = session;
    }

    /** Kaivaa sessionId:lla session-olion.
     * @param sessionId sessionId
     * @return sesson-olio
     */
    @Override
    public final synchronized HttpSession getHttpSession(
            final String sessionId
    ) {
        return httpSessions.get(sessionId);
    }

    /** Palauttaa viimeisimman sessio-olion testausta varten.
     * @return sessio-olio
     */
    @Override
    public final synchronized HttpSession getLatestHttpSession() {
        return latestSession;
    }

    /** Metodi paivittaa tarvittaessa session-attribuuttien state,
     * userId ja username vastaamaan ajanmukaisia arvoja.
     * @param session session
     * @param professional professional
     */
    @Override
    public final synchronized void updateSessionAttributes(
            final HttpSession session,
            final Principal professional
    ) {
        /** Kaivetaan username ja id sessio-attribuuteista. */
        Object username = session.getAttribute("username");
        Object userId = session.getAttribute("userId");

        /** Paivitetaan muuttujat, jos tarpeellista. */
        if (professional != null) {
            /* Jos client on autentikoitunut ammattilaiseksi */
            username = professional.getName();
            userId = mapperService.getIdFromRegisteredName(username.toString());
            session.setAttribute("state", "pro");
            updateSessionChannels(session);
        } else if (get(session, "username").isEmpty()) {
            /* Uusi kayttaja */
            username = "Anon";
            userId = mapperService.generateNewId();
            session.setAttribute("state", "start");
            session.setAttribute("category", "Kategoria"); //TODO
            String randomNewChannel = mapperService.generateNewId();
            session.setAttribute("channelId", randomNewChannel);
        }

        /** Liitetaan muuttujien tieto sessioon (monesti aiemman paalle). */
        session.setAttribute("username", username);
        session.setAttribute("userId", userId);

        /** Kirjataan tiedot mapperiin (monesti aiemman paalle). */
        mapperService.mapUsernameToId(userId.toString(), username.toString());
    }

    /** Kun ammattilaiskayttaja avaa uuden kanavan.
     * @param session sessio-olio
     * @param channelId channelId
     */
    @Override
    public final synchronized void addChannel(
            final HttpSession session,
            final String channelId
    ) {
        if (session.getAttribute("channelIds") != null) {
            /** Case: pro user with multiple channels. */
            HashSet<String> channels = proChannels.get(session.getId());
            if (channels == null) {
                channels = new LinkedHashSet<>();
                proChannels.put(session.getId(), channels);
            }
            channels.add(channelId);
            updateSessionChannels(session);
        } else {
            /** Case: regular user with single channel. Never called? */
            session.setAttribute("channelId", channelId);
        }
    }

    /** Kun ammattilaiskayttaja sulkee kanavan.
     * TODO: Mieti, mita muuta tassa yhteydessa pitaisi tehda.
     * @param session session
     * @param channelId closed channel Id.
     */
    @Override
    public final synchronized void removeChannel(
            final HttpSession session,
            final String channelId
    ) {
        if (session.getAttribute("channelIds") != null) {
            /** Case: pro user with multiple channels. */
            HashSet<String> channels = proChannels.get(session.getId());
            if (channels != null) {
                channels.remove(channelId);
            }
            updateSessionChannels(session);
        }
    }

    /** Kutsu tata metodia proChannels -paivityksen jalkeen.
     * Metodi paivittaa sessionsin tiedot proChannelsin tietojen perusteella.
     * @param session session
     */
    private synchronized void updateSessionChannels(
            final HttpSession session
    ) {
        HashSet<String> channels = proChannels.get(session.getId());
        String channelIds = getChannelsAsJsonFriendly(channels);
        session.setAttribute("channelIds", channelIds);
    }

    /** Annettuna setti kanavia, tuottaa Stringin halutussa muotoilussa.
     * @param channels sdfdf
     * @return rrrrg
     */
    private synchronized String getChannelsAsJsonFriendly(
            final HashSet<String> channels
    ) {
        if (channels == null || channels.isEmpty()) {
            return "[]";
        }
        String output = "[";
        for (String channel : channels) {
            output += "\"" + channel + "\", ";
        }
        System.out.println("Output orig : " + output);
        System.out.println("Length orig : " + output.length());
        output = output.substring(0, output.length() - 2);
        System.out.println("after sub: " + output);
        output += "]";
        return output;
    }


}
