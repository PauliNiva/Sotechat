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

    private HashMap<String, HttpSession> httpSessions;

    /** Mapperilta voi esim. kysyä "mikä username on ID:llä x?". */
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
    }

    @Override
    public HttpSession getHttpSession(String sessionId) {
        return httpSessions.get(sessionId);
    }

    /** Metodi päivittää tarvittaessa session-attribuuttien state,
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

        /** Päivitetään muuttujat, jos tarpeellista. */
        if (professional != null) {
            /* Jos client on autentikoitunut ammattilaiseksi */
            username = professional.getName();
            userId = mapperService.getIdFromRegisteredName(username.toString());
            session.setAttribute("state", "notRelevantForProfessional");
            session.setAttribute("category", "notRelevantForProfessional");
            session.setAttribute("channelIds", "DEV_CHANNEL"); // TODO
        } else if (get(session, "username").isEmpty()) {
            /* Uusi käyttäjä */
            username = "Anon";
            userId = mapperService.generateNewId();
            session.setAttribute("state", "start");
            session.setAttribute("category", "DRUGS"); //TODO

            /** Oikea kanavaID annetaan vasta nimen/aloitusviestin jälkeen. */
            String channelNotRelevantYet = mapperService.generateNewId();
            session.setAttribute("channelId", channelNotRelevantYet);
            /** Random kanava failsafena, jos jonkin virheen vuoksi
             * käyttäjät päätyisivätkin sinne keskustelemaan,
             * tyhjä kanava on parempi kuin kasa trolleja. */
        }

        /** Liitetään muuttujien tieto sessioon (monesti aiemman päälle). */
        session.setAttribute("username", username);
        session.setAttribute("userId", userId);

        /** Kirjataan tiedot mapperiin (monesti aiemman päälle). */
        mapperService.mapUsernameToId(userId.toString(), username.toString());
    }


}
