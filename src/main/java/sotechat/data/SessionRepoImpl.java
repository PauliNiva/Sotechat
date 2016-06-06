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

    @Autowired
    public SessionRepoImpl(
            final Mapper pMapper
    ) {
        super();
        this.mapperService = pMapper;
        this.httpSessions = new HashMap<>();
        this.proChannels = new HashMap<>();
    }

    @Override
    public void
    mapHttpSessionToSessionId(String sessionId, HttpSession session) {
        this.httpSessions.put(sessionId, session);
        this.latestSession = session;
    }

    @Override
    public HttpSession getHttpSession(String sessionId) {
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
            String channelIds = jsonFriendlyFormat(proChannels.get(session.getId()));
            session.setAttribute("channelIds", channelIds);
        } else if (get(session, "username").isEmpty()) {
            /* Uusi kayttaja */
            username = "Anon";
            userId = mapperService.generateNewId();
            session.setAttribute("state", "start");
            session.setAttribute("category", "DRUGS"); //TODO

            /** Oikea kanavaID annetaan vasta nimen/aloitusviestin jalkeen. */
            String channelNotRelevantYet = mapperService.generateNewId();
            session.setAttribute("channelId", channelNotRelevantYet);
            /** Random kanava failsafena, jos jonkin virheen vuoksi
             * kayttajat paatyisivatkin sinne keskustelemaan,
             * tyhja kanava on parempi kuin kasa trolleja. */
        }

        /** Liitetaan muuttujien tieto sessioon (monesti aiemman paalle). */
        session.setAttribute("username", username);
        session.setAttribute("userId", userId);

        /** Kirjataan tiedot mapperiin (monesti aiemman paalle). */
        mapperService.mapUsernameToId(userId.toString(), username.toString());
    }

    @Override
    public void addChannel(HttpSession session, String channelId) {
        if (session.getAttribute("channelIds") != null) {
            /** Case: pro user with multiple channels. */
            HashSet<String> channels = proChannels.get(session.getId());
            if (channels == null) {
                channels = new HashSet<>();
                proChannels.put(session.getId(), channels);
            }
            channels.add(channelId);
            String channelIds = jsonFriendlyFormat(channels);
            session.setAttribute("channelIds", channelIds);
        } else {
            /** Case: regular user with single channel. */
            session.setAttribute("channelId", channelId);
        }
    }

    //@Override TODO
    public void removeChannel(HttpSession session, String channelId) {

    }

    /** Annettuna setti kanavia, tuottaa Stringin halutussa muotoilussa.
     * @param channels sdfdf
     * @return rrrrg
     */
    private String jsonFriendlyFormat(HashSet<String> channels) {
        if (channels == null || channels.isEmpty()) return "[]";
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
