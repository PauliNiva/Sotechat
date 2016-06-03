package sotechat.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

import sotechat.JoinResponse;
import sotechat.ProStateResponse;
import sotechat.UserStateResponse;
import sotechat.data.Mapper;
import sotechat.service.QueueService;
import sotechat.service.StateService;

import static sotechat.service.StateService.get;

/** Reititys tilaan liittyville HTML GET- ja POST-pyynnoille.
 */
public class StateController {

    /** Mapperilta voi esim. kysyä "mikä username on ID:llä x?". */
    private final Mapper mapper;

    /** QueueService. */
    private final QueueService queueService;

    /** State Service. */
    private final StateService stateService;

    /** Channel where queue status is broadcasted. */
    private static final String QUEUE_BROADCAST_CHANNEL = "QBCC";

    /** SubScribeEventHandler. */
    private final ApplicationListener subscribeEventListener;

    /** Spring taikoo tässä Singleton-instanssit palveluista.
     *
     * @param pMapper Olio, johon talletetaan tiedot käyttäjien id:istä
     * ja käyttäjänimistä, ja josta voidaan hakea esim. käyttäjänimi
     * käyttäjä-id:n perusteella.
     * @param queueService queueService
     * @param subscribeEventListener dfojfdoidfjo
     * @param stateService ssdofofsd
     */
    @Autowired
    public StateController(
            final Mapper pMapper,
            final ApplicationListener subscribeEventListener,
            final QueueService queueService,
            final StateService stateService
    ) {
        this.mapper = pMapper;
        this.subscribeEventListener = subscribeEventListener;
        this.queueService = queueService;
        this.stateService = stateService;
    }

    /** Kun customerClient haluaa pyytää tilan (mm. sivun latauksen yhteydessä).
     * @param req täältä päästään session-olioon käsiksi.
     * @return mitä vastataan customerClientin tilanpäivityspyyntöön.
     * @throws Exception mikä poikkeus
     */
    @RequestMapping(value = "/userState", method = RequestMethod.GET)
    public final UserStateResponse returnUserStateResponse(
            final HttpServletRequest req
            ) throws Exception
    {
        HttpSession session = req.getSession();
        System.out.println("     State request from customerClient " + session.getId());
        return stateService.respondToUserStateRequest(session);
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
            ) throws Exception
    {
        HttpSession session = req.getSession();
        System.out.println("     State request from proClient " + session.getId());
        return stateService.respondToProStateRequest(session, professional);
    }


    /** Kun client lähettää avausviestin ja haluaa liittyä pooliin.
     * @param request request
     * @return mitä vastataan clientille
     * @throws Exception mikä poikkeus
     */
    @RequestMapping(value = "/joinPool", method = RequestMethod.POST)
    public final String respondToJoinPoolRequest(
            final HttpServletRequest request
            ) throws Exception
    {
        HttpSession session = request.getSession();
        return stateService.respondToJoinPoolRequest(request);
    }



    /** Kun hoitaja ottaa jonosta chatin, tänne pitäisi tulla signaali.
     * @param accessor accessor
     * @param channelId channelId
     * @return Palautusarvo kuljetetaan "jonotuskanavan" kautta jonottajalle.
     * @throws Exception mikä poikkeus
     */
    @MessageMapping("/toServer/queue/{channelId}")
    @SendTo("/toClient/queue/{channelId}")
    public final String popClientFromQueue(
            final SimpMessageHeaderAccessor accessor,
            final @DestinationVariable String channelId
    ) throws Exception {
        //upgradeSubscribersToChat(channelId);
        // TODO: Poista jonosta kaikki jotka on subscribennu kanavaan channelId
        System.out.println("Opening channel Id: " + channelId);
        return "{\"content\":\"channel activated. request new state now.\"}";
    }






    /** EI KÄYTÖSSÄ ENÄÄ ?
     *
     * Kun client menee sivulle index.html, tiedostoon upotettu
     * JavaScript tekee erillisen GET-pyynnön polkuun /join.
     * Tällä pyynnöllä client ilmaisee haluavansa chattiin.
     * Alla oleva metodi mappaa pyynnöt polkuun /join
     * ja palauttaa käyttäjälle JSONina usernamen, userId:n
     * ja kanavaId:n (kaikki samalle kanavalle DEV_CHANNEL).
     * @return Palautusarvo lähetetään JSONina clientille.
     * @throws Exception mikä poikkeus?
     * @param req Http GET-pyyntö osoitteesee /join.
     * @param professional Kirjautuneelle käyttäjälle(hoitaja) luotu
     *                     istunto(session) kirjautumisen yhteydessä
     */
    @RequestMapping(value = "/join", method = RequestMethod.GET)
    public final JoinResponse returnJoinResponse(
            final HttpServletRequest req, final Principal professional)
            throws Exception {
        HttpSession session = req.getSession();

        System.out.println("     id(join) = " + session.getId());

        stateService.updateSessionAttributes(session, professional);
        String username = session.getAttribute("username").toString();
        String userId = session.getAttribute("userId").toString();

        /** Palautetaan JoinResponse, jonka Spring paketoi JSONiksi. */
        return new JoinResponse(username, userId, "DEV_CHANNEL");
    }
}
