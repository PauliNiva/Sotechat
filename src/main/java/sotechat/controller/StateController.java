package sotechat.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sotechat.service.QueueService;
import sotechat.service.ValidatorService;
import sotechat.wrappers.ProStateResponse;
import sotechat.wrappers.UserStateResponse;

/** Reititys tilaan liittyville pyynnoille (GET, POST, WS).
 */
@RestController
public class StateController {

    /** Validator Service. */
    private final ValidatorService validatorService;

    /** Session Repository. */
    private final SessionRepo sessionRepo;

    /** QueueService. */
    private final QueueService queueService;

    /** Queue Broadcaster. */
    private final QueueBroadcaster queueBroadcaster;

    /** Message Broker. */
    private final SimpMessagingTemplate broker;


    /** Spring taikoo tassa Singleton-instanssit palveluista.
     * @param pValidatorService validatorService
     * @param pSessionRepo sessionRepo
     * @param pQueueService queueService
     * @param pQueueBroadcaster queueBroadCaster
     * @param pBroker broker
     */
    @Autowired
    public StateController(
            final ValidatorService pValidatorService,
            final SessionRepo pSessionRepo,
            final QueueService pQueueService,
            final QueueBroadcaster pQueueBroadcaster,
            final SimpMessagingTemplate pBroker
    ) {
        this.validatorService = pValidatorService;
        this.sessionRepo = pSessionRepo;
        this.queueService = pQueueService;
        this.queueBroadcaster = pQueueBroadcaster;
        this.broker = pBroker;
    }

    /** Kun normikayttaja haluaa pyytaa tilan (mm. sivun latauksen yhteydessa).
     * @param req taalta paastaan session-olioon kasiksi.
     * @param professional autentikointitiedot
     * @return JSON-muotoon paketoitu UserStateResponse.
     *          Palautusarvoa ei kayteta kuten yleensa metodin palautusarvoa,
     *          vaan se lahetetaan HTTP-vastauksena pyynnon tehneelle
     *          kayttajalle.
     */
    @RequestMapping(value = "/userState", method = RequestMethod.GET)
    public final UserStateResponse returnUserStateResponse(
            final HttpServletRequest req,
            final Principal professional
    ) {
        Session session = sessionRepo.updateSession(req, professional);
        return new UserStateResponse(session);
    }

    /** Kun proClient haluaa pyytaa tilan (mm. sivun lataus).
     * @param req taalta paastaan session-olioon kasiksi.
     * @param professional kirjautumistiedot
     * @return JSON-muotoon paketoitu ProStateResponse (tai null).
     *          Palautusarvoa ei kayteta kuten yleensa metodin palautusarvoa,
     *          vaan se lahetetaan HTTP-vastauksena pyynnon tehneelle
     *          kayttajalle.
     */
    @RequestMapping(value = "/proState", method = RequestMethod.GET)
    public final ProStateResponse returnProStateResponse(
            final HttpServletRequest req,
            final Principal professional
    ) {
        if (professional == null) {
            /** Hacking attempt? */
            return null;
        }
        Session session = sessionRepo.updateSession(req, professional);
        return new ProStateResponse(session);
    }

    /** Kun client lahettaa avausviestin ja haluaa liittya pooliin.
     * @param request pyynnon tiedot
     * @param professional autentikaatiotiedot
     * @return JSON {"content":"Denied..."} tai {"content":"OK..."}
     *          Palautusarvoa ei kayteta kuten yleensa metodin palautusarvoa,
     *          vaan se lahetetaan HTTP-vastauksena pyynnon tehneelle
     *          kayttajalle.
     */
    @RequestMapping(value = "/joinPool", method = RequestMethod.POST)
    public final String respondToJoinPoolRequest(
            final HttpServletRequest request,
            final Principal professional
    ) {
        String response = processJoinPoolReq(request, professional);
        return "{\"content\":\"" + response + "\"}";
    }

    /** Validoi pyynto liittya jonoon ja suorita se.
     * @param req pyynnon tiedot
     * @param auth autentikaatiotiedot
     * @return String "OK..." tai "Denied..."
     */
    private String processJoinPoolReq(
            final HttpServletRequest req,
            final Principal auth
    ) {

        /** Tehdaan JSON-objekti clientin lahettamasta JSONista. */
        JsonObject payload;
        try {
            String jsonString = req.getReader().readLine();
            JsonParser parser = new JsonParser();
            payload = parser.parse(jsonString).getAsJsonObject();
        } catch (Exception e) {
            return "Denied due to invalid JSON formatting.";
        }

        /** Validointi. */
        String error = validatorService.validateJoin(req, payload, auth);
        if (!error.isEmpty()) {
            return error;
        }

        /** Suorittaminen. */
        queueService.joinQueue(req, payload);
        queueBroadcaster.broadcastQueue();
        return "OK, please request new state now.";
    }

    /** Kun hoitaja yrittaa ottaa jonosta uuden chatin, client lahettaa
     * subscribe-pyynnon /queue/id/ ja tama metodi aktivoituu.
     *
     *  Toimenpiteet mita tehdaan:
     *  - Poistetaan jonosta kyseinen chatti.
     *  - Broadcastataan jonon uusi tila hoitajille
     *  - Kerrotaan kaikille asianomaisille (/queue/id/ subscribaajille),
     *    kenelle hoitajalle kanava kuuluu. Huomaa palautusarvon selitys!
     * @param channelId channelId
     * @param accessor accessor
     * @return Palautusarvo lahetetaan JSONina jonotuskanavalle.
     *          esim. {"channel assigned to":"Hoitaja Anne"}
     *          Kayttotapauksia viestille:
     *          - Kerrotaan jonottajalle, etta chatti on auki
     *          - Poppausta yrittanyt client saa kuulla, etta poppaus onnistui
     *          - Poppausta yrittanyt client saa kuulla, etta joku toinen ehti
     *              juuri popata ennen meita.
     */
    @MessageMapping("/toServer/queue/{channelId}")
    @SendTo("/toClient/queue/{channelId}")
    public final String popClientFromQueue(
            final @DestinationVariable String channelId,
            final SimpMessageHeaderAccessor accessor
    ) {

        /** Varmista, etta poppaaja on autentikoitunut. */
        if (accessor.getUser() == null) {
            System.out.println("Hacking attempt?");
            return "";
        }

        /** Yritetaan popata ja haetaan username kenelle kanava on popattu. */
        String assignee = queueService.popQueue(channelId, accessor);

        /** Broadcastataan jonon tila kaikille ammattilaisille.
         * HUOM: Ei broadcastata viesteja viela. Vasta kun joku subscribaa. */
        queueBroadcaster.broadcastQueue();

        /** Palautetaan asianomaisille tieto, kenelle kanava on popattu. */
        return "{\"channelAssignedTo\":\"" + assignee + "\"}";
    }

    /** Pyynto poistua chat-kanavalta (tavallinen tai ammattilaiskayttaja).
     * @param req req
     * @param pro pro
     * @param channelId channelId
     */
    @RequestMapping(value = "/leave/{channelId}", method = RequestMethod.POST)
    public final void leaveChat(
            final @PathVariable String channelId,
            final HttpServletRequest req,
            final Principal pro
    ) {
        String sessionId = req.getSession().getId();
        if (!validatorService.validateLeave(sessionId, pro, channelId)) {
            return;
        }
        sessionRepo.leaveChannel(channelId, sessionId);

        /** Suljetaan kanava, kun kuka tahansa lahtee.
         * Jatetaan se kuitenkin auki hoitajan valilehtiin. */
        String channelIdWithPath = "/toClient/chat/" + channelId;
        String closedChannelNotice = "{\"notice\":\"chat closed\"}";
        broker.convertAndSend(channelIdWithPath, closedChannelNotice);
    }

}
