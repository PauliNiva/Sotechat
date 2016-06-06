package sotechat.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;

import static sotechat.util.Utils.get;

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
    public final void
    mapHttpSessionToSessionId(
            String sessionId,
            HttpSession session
    ) {
        this.httpSessions.put(sessionId, session);
        this.latestSession = session;
    }

    /** Kaivaa sessionId:lla session-olion.
     * @param sessionId sessionId
     * @return sesson-olio
     */
    @Override
    public final HttpSession getHttpSession(
            String sessionId
    ) {
        return httpSessions.get(sessionId);
    }

    @Override
    public HttpSession getLatestHttpSession() {
        return latestSession;
    }

    /** Metodi paivittaa tarvittaessa session-attribuuttien state,
     * userId ja username vastaamaan ajanmukaisia arvoja.
     * @param session session
     * @param professional professional
     */
    @Override
    public final void updateSessionAttributes(
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
            session.setAttribute("state", "notRelevantForProfessional");
            session.setAttribute("category", "notRelevantForProfessional");
            updateSessionChannels(session);
        } else if (get(session, "username").isEmpty()) {
            /* Uusi kayttaja */
            username = "Anon";
            userId = mapperService.generateNewId();
            session.setAttribute("state", "start");
            session.setAttribute("category", "DRUGS"); //TODO
            String randomNewChannel = mapperService.generateNewId();
            session.setAttribute("channelId", randomNewChannel);
        }

        /** Liitetaan muuttujien tieto sessioon (monesti aiemman paalle). */
        session.setAttribute("username", username);
        session.setAttribute("userId", userId);

        /** Kirjataan tiedot mapperiin (monesti aiemman paalle). */
        mapperService.mapUsernameToId(userId.toString(), username.toString());
    }

    /** When pro user opened a new chat.
     * @param session session
     * @param channelId channelId
     */
    @Override
    public final void addChannel(
            final HttpSession session,
            final String channelId
    ) {
        if (session.getAttribute("channelIds") != null) {
            /** Case: pro user with multiple channels. */
            HashSet<String> channels = proChannels.get(session.getId());
            if (channels == null) {
                channels = new HashSet<>();
                proChannels.put(session.getId(), channels);
            }
            channels.add(channelId);
            updateSessionChannels(session);
        } else {
            /** Case: regular user with single channel. Never called? */
            session.setAttribute("channelId", channelId);
        }
    }

    /** When pro user closes a chat window.
     * @param session session
     * @param channelId closed channel Id.
     */
    @Override
    public final void removeChannel(
            HttpSession session,
            String channelId
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
    private final void updateSessionChannels(
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
    private String getChannelsAsJsonFriendly(
            HashSet<String> channels
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
