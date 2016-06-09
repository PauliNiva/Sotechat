package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sotechat.wrappers.ProStateResponse;
import sotechat.wrappers.UserStateResponse;
import sotechat.websocketService.StateService;

/** Reititys tilaan liittyville pyynnoille (GET, POST, WS).
 */
@RestController
public class StateController {

    /** State Service. */
    private final StateService stateService;

    /** Queue Broadcaster. */
    private final QueueBroadcaster queueBroadcaster;

    /** Testi. */
    private final ChatLogBroadcaster chatLogBroadcaster;


    /** Spring taikoo tassa Singleton-instanssit palveluista.
     * @param pStateService stateService
     * @param pQueueBroadcaster queue Broadcaster
     * @param pChatLogBroadcaster chatLogBroadcaster
     */
    @Autowired
    public StateController(
            final StateService pStateService,
            final QueueBroadcaster pQueueBroadcaster,
            final ChatLogBroadcaster pChatLogBroadcaster
    ) {
        this.stateService = pStateService;
        this.queueBroadcaster = pQueueBroadcaster;
        this.chatLogBroadcaster = pChatLogBroadcaster;
    }

    /** Kun customerClient haluaa pyytaa tilan (mm. sivun latauksen yhteydessa).
     * @param req taalta paastaan session-olioon kasiksi.
     * @param professional autentikointitiedot
     * @return JSON-muotoon paketoitu UserStateResponse.
     *          Palautusarvoa ei kayteta kuten yleensa metodin palautusarvoa,
     *          vaan se lahetetaan HTTP-vastauksena pyynnon tehneelle
     *          kayttajalle.
     * @throws Exception mika poikkeus
     */
    @RequestMapping(value = "/userState", method = RequestMethod.GET)
    public final UserStateResponse returnUserStateResponse(
            final HttpServletRequest req,
            final Principal professional
            ) throws Exception {

        return stateService.respondToUserStateRequest(req, professional);
    }

    /** Kun proClient haluaa pyytaa tilan (mm. sivun lataus).
     * @param req taalta paastaan session-olioon kasiksi.
     * @param professional kirjautumistiedot
     * @return JSON-muotoon paketoitu ProStateResponse (tai null).
     *          Palautusarvoa ei kayteta kuten yleensa metodin palautusarvoa,
     *          vaan se lahetetaan HTTP-vastauksena pyynnon tehneelle
     *          kayttajalle.
     * @throws Exception mika poikkeus
     */
    @RequestMapping(value = "/proState", method = RequestMethod.GET)
    public final ProStateResponse returnProStateResponse(
            final HttpServletRequest req,
            final Principal professional
            ) throws Exception {
        if (professional == null) {
            /** Hacking attempt? */
            return null;
        }
        return stateService.respondToProStateRequest(req, professional);
    }


    /** Kun client lahettaa avausviestin ja haluaa liittya pooliin.
     * @param request session tiedot
     * @param professional autentikaatiotiedot
     * @return JSON {"content":"Denied..."} tai {"content":"OK..."}
     *          Palautusarvoa ei kayteta kuten yleensa metodin palautusarvoa,
     *          vaan se lahetetaan HTTP-vastauksena pyynnon tehneelle
     *          kayttajalle.
     * @throws Exception mika poikkeus
     */
    @RequestMapping(value = "/joinPool", method = RequestMethod.POST)
    public final String respondToJoinPoolRequest(
            final HttpServletRequest request,
            final Principal professional
            ) throws Exception {

        if (professional != null) {
            /** Hoitaja yrittaa liittya pooliin asiakkaana. */
            return "{\"content\":\"Denied join "
                    + "pool request for professional.\"}";
        }
        String answer = stateService.respondToJoinPoolRequest(request);
        queueBroadcaster.broadcastQueue();
        return "{\"content\":\"" + answer + "\"}";
    }

    /** Hoitaja avaa jonosta chatin, JS-WebSocket subscribaa /queue/id/.
     *  (Tama metodi jostain syysta aktivoituu, vaikka kyseessa ei ole viesti.)
     *  Toimenpiteet mita tehdaan:
     *  - Poistetaan jonosta olio
     *  - Broadcastataan jonon uusi tila hoitajille
     *  - Heratellaan avatun kanavan osalliset (eli yksi jonottaja)
     * @param channelId channelId
     * @param accessor accessor
     * @return Joko tyhja String "" tai JSON {"content":"channel activated."}
     *          Palautusarvo kuljetetaan "jonotuskanavan" kautta jonottajalle.
     * @throws Exception mika poikkeus
     */
    @MessageMapping("/toServer/queue/{channelId}")
    @SendTo("/toClient/queue/{channelId}")
    public final String popClientFromQueue(
            final @DestinationVariable String channelId,
            final SimpMessageHeaderAccessor accessor
            ) throws Exception {

        /** Varmista, etta poppaaja on autentikoitunut. */
        if (accessor.getUser() == null) {
            System.out.println("Hacking attempt?");
            return "";
        }
        String wakeUp = stateService.popQueue(channelId, accessor);
        if (wakeUp.isEmpty()) {
            /** Case: Toinen professional ehtikin popata taman ennen meita. */
            return "";
        }
        queueBroadcaster.broadcastQueue();
        return wakeUp;
    }

}
