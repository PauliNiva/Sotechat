package sotechat.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

import sotechat.JoinResponse;
import sotechat.ProStateResponse;
import sotechat.UserStateResponse;
import sotechat.data.SessionRepo;
import sotechat.service.StateService;

/** Reititys tilaan liittyville pyynnöille (GET, POST, WS).
 */
@RestController
public class StateController {

    /** State Service. */
    private final StateService stateService;

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;


    /** Spring taikoo tässä Singleton-instanssit palveluista.
     *
     * @param pStateService ssdofofsd
     */
    @Autowired
    public StateController(
            final StateService pStateService
    ) {
        this.stateService = pStateService;
    }

    /** Kun customerClient haluaa pyytää tilan (mm. sivun latauksen yhteydessä).
     * @param req täältä päästään session-olioon käsiksi.
     * @return mitä vastataan customerClientin tilanpäivityspyyntöön.
     * @throws Exception mikä poikkeus
     */
    @RequestMapping(value = "/userState", method = RequestMethod.GET)
    public final UserStateResponse returnUserStateResponse(
            final HttpServletRequest req,
            final Principal professional
            ) throws Exception {

        return stateService.respondToUserStateRequest(req, professional);
    }

    /** Kun proClient haluaa pyytää tilan (mm. sivun lataus).
     * @param req täältä päästään session-olioon käsiksi.
     * @param professional kirjautumistiedot
     * @return mitä vastataan proClientin tilanpäivityspyyntöön.
     * @throws Exception mikä poikkeus
     */
    @RequestMapping(value = "/proState", method = RequestMethod.GET)
    public final ProStateResponse returnProStateResponse(
            final HttpServletRequest req,
            final Principal professional
            ) throws Exception {

        return stateService.respondToProStateRequest(req, professional);
    }


    /** Kun client lähettää avausviestin ja haluaa liittyä pooliin.
     * @param request request
     * @return mitä vastataan clientille
     * @throws Exception mikä poikkeus
     */
    @RequestMapping(value = "/joinPool", method = RequestMethod.POST)
    public final String respondToJoinPoolRequest(
            final HttpServletRequest request
            ) throws Exception {

        return stateService.respondToJoinPoolRequest(request);
    }



    /** Hoitaja avaa jonosta chatin, JS-WebSocket lähettää jotain /queue/id/
     *  -> Poistetaan jonosta olio
     *  -> Broadcastataan jonon uusi tila hoitajille
     *  -> Herätellään avatun kanavan osalliset (yksi jonottaja)
     * @param channelId channelId
     * @return Palautusarvo kuljetetaan "jonotuskanavan" kautta jonottajalle.
     * @throws Exception mikä poikkeus
     */
    @MessageMapping("/toServer/queue/{channelId}")
    @SendTo("/toClient/queue/{channelId}")
    public final String popClientFromQueue(
            final @DestinationVariable String channelId
            ) throws Exception {

        String wakeUp = stateService.popQueue(channelId);
        String qbcc = "/" + stateService.getQueueBroadcastChannel();
        String qAsJson = stateService.getQueueAsJson();
        brokerMessagingTemplate.convertAndSend(qbcc, qAsJson); //TODO:TEST THIS
        return wakeUp;
    }

}
