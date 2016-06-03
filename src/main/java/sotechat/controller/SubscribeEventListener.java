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

import static sotechat.service.StateService.get;

/** Used to keep track who is subscribed to which channel.
 *
 */
@Component
public class SubscribeEventListener
        implements ApplicationListener<ApplicationEvent> {

    /** Key = channelID, value = list of subscribed sessions. */
    private HashMap<String, List<HttpSession>> map;

    /** Session Repository. */
    @Autowired
    private SessionRepo sessionRepo;

    /** Constructor initiates singleton instance. */
    public SubscribeEventListener() {
        map = new HashMap<String, List<HttpSession>>();
    }


    /** Delegates jobs to proper handler methods.
     * @param applicationEvent all application events routed through here.
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

    /** Handles subscribe events.
     * @param event event
     */
    private void handleSubscribe(final SessionSubscribeEvent event) {
      //  System.out.println("SUB = " + event.toString());
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

        if (!map.get(channelId).isEmpty() || map.get(channelId) != null) {
            map.get(channelId).add(session);
        } else {
            List<HttpSession> sessions = new ArrayList();
            sessions.add(session);
            map.put(session.getAttribute("channelId").toString(), sessions);
        }
    }

    /** Handles unsubscribe events.
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
