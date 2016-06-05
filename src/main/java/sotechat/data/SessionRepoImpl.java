package sotechat.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import static sotechat.util.Utils.get;

@Component
public class SessionRepoImpl extends MapSessionRepository
    implements SessionRepo {

    /** Avain sessio-ID, arvo HttpSession-olio. */
    private HashMap<String, HttpSession> httpSessions;

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
        this.httpSessions = new HashMap();
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
            session.setAttribute("channelIds", "[\"Autot\", \"Mopot\"]"); // TODO
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


}
