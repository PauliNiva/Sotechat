package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import sotechat.data.SessionRepo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static sotechat.util.Utils.get;

/** Kuuntelee WebSocket subscribe/unsubscribe -tapahtumia
 * ja pitaa kirjaa, ketka kuuntelevat mitakin kanavaa.
 */
@Component
public class SubscribeEventListener
        implements ApplicationListener<ApplicationEvent> {

    /** Key = channelID, value = list of subscribed sessions. */
    private HashMap<String, List<HttpSession>> map;

    /** Session Repository. */
    @Autowired
    private SessionRepo sessionRepo;

    /** Vain 1 instanssi. */
    public SubscribeEventListener() {
        map = new HashMap<String, List<HttpSession>>();
    }

    public List<HttpSession> getSubscribers(String channelId) {
        List<HttpSession> subs = map.get(channelId);
        if (subs == null) {
            subs = new ArrayList<HttpSession>();
        }
        return subs;
    }


    /** Siirtaa tehtavat "kasittele sub" ja "kasittele unsub" oikeille metodeil.
     * @param applicationEvent kaikki applikaatioEventit aktivoivat taman.
     */
    @Override
    public final void onApplicationEvent(
            final ApplicationEvent applicationEvent
    ) {
        if (applicationEvent.getClass() == SessionSubscribeEvent.class) {
            handleSubscribe((SessionSubscribeEvent) applicationEvent);
        } else if
                (applicationEvent.getClass() == SessionUnsubscribeEvent.class) {
            handleUnsubscribe((SessionUnsubscribeEvent) applicationEvent);
        }
    }

    /** Kasittelee subscribe -tapahtumat.
     * @param event event
     */
    private void handleSubscribe(final SessionSubscribeEvent event) {
        System.out.println("SUB = " + event.toString());
        MessageHeaders headers = event.getMessage().getHeaders();
        String sessionId = SimpMessageHeaderAccessor
                .getSessionAttributes(headers)
                .get("SPRING.SESSION.ID").toString();

        HttpSession session = sessionRepo.getHttpSession(sessionId);

        String channelId = get(session, "channelId");
        System.out.println("Subscribing someone to " + channelId);
        if (channelId.isEmpty()) {
            return;
        }

        /** Add session to list of subscribers to channelId. */
        List<HttpSession> list = map.get(channelId);
        if (list == null) {
            list = new ArrayList<>();
            map.put(channelId, list);
        }
        list.add(session);
    }

    /** Kasittelee unsubscribe -tapahtumat.
     * @param event event
     */
    private void handleUnsubscribe(final SessionUnsubscribeEvent event) {
        System.out.println("UNSUB = " + event.toString());
    }

    /** Required for dependency injection in this case.
     * @param repo repo
     */
    private void setSessionRepo(final SessionRepo repo) {
        this.sessionRepo = repo;
    }

    /** Required for dependency injection in this case. */
    private SessionRepo getSessionRepo() {
        return this.sessionRepo;
    }
}
