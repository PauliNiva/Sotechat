package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import sotechat.wrappers.ProStateResponse;
import sotechat.wrappers.UserStateResponse;
import sotechat.service.StateService;

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
     * @param pStateService state service
     * @param pQueueBroadcaster queue Broadcaster
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
     * @return mita vastataan customerClientin tilanpaivityspyyntoon.
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
     * @return mita vastataan proClientin tilanpaivityspyyntoon.
     * @throws Exception mika poikkeus
     */
    @RequestMapping(value = "/proState", method = RequestMethod.GET)
    public final ProStateResponse returnProStateResponse(
            final HttpServletRequest req,
            final Principal professional
            ) throws Exception {

        return stateService.respondToProStateRequest(req, professional);
    }


    /** Kun client lahettaa avausviestin ja haluaa liittya pooliin.
     * @param request request
     * @return mita vastataan clientille
     * @throws Exception mika poikkeus
     */
    @RequestMapping(value = "/joinPool", method = RequestMethod.POST)
    public final String respondToJoinPoolRequest(
            final HttpServletRequest request
            ) throws Exception {

        String answer = stateService.respondToJoinPoolRequest(request);
        queueBroadcaster.broadcastQueue();
        return answer;
    }



    /** Hoitaja avaa jonosta chatin, JS-WebSocket lahettaa jotain /queue/id/
     *  Tama metodi aktivoituu, kun kyseinen signaali saapuu palvelimelle.
     *  Toimenpiteet mita tehdaan:
     *  -> Poistetaan jonosta olio
     *  -> Broadcastataan jonon uusi tila hoitajille
     *  -> Heratellaan avatun kanavan osalliset (eli yksi jonottaja)
     * @param channelId channelId
     * @param accessor accessor
     * @return Palautusarvo kuljetetaan "jonotuskanavan" kautta jonottajalle.
     * @throws Exception mika poikkeus
     */
    @MessageMapping("/toServer/queue/{channelId}")
    @SendTo("/toClient/queue/{channelId}")
    public final String popClientFromQueue(
            final @DestinationVariable String channelId,
            final SimpMessageHeaderAccessor accessor
            ) throws Exception {
        String wakeUp = stateService.popQueue(channelId, accessor);
        queueBroadcaster.broadcastQueue();
        return wakeUp;
    }

}
