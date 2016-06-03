package sotechat.controller;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

/** Used to keep track who is subscribed to which channel.
 *
 */
@Component
public class SubscribeEventHandler
        implements ApplicationListener<ApplicationEvent> {

    /** Key = channelID, value = list of subscribed sessions. */
    private HashMap<String, List<HttpSession>> map;

    /** Constructor initiates singleton instance. */
    public SubscribeEventHandler() {
        map = new HashMap<String, List<HttpSession>>();
    }


    /** Delegates jobs to proper handler methods.
     * @param applicationEvent all application events routed through here.
     */
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
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
        System.out.println("SUB = " + event.toString());
    }

    /** Handles unsubscribe events.
     * @param event event
     */
    private void handleUnsubscribe(final SessionUnsubscribeEvent event) {
        System.out.println("UNSUB = " + event.toString());
    }
}
