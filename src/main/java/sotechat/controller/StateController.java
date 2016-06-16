package sotechat.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

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

    /** Subscribe Event Listener. */
 //   private final SubscribeEventListener subscribeEventListener;

    /** Session Repository. */
    private final SessionRepo sessionRepo;

    /** QueueService. */
    private final QueueService queueService;

    /** Queue Broadcaster. */
    private final QueueBroadcaster queueBroadcaster;


    /** Spring taikoo tassa Singleton-instanssit palveluista.
     * @param pSessionRepo sessionRepo
     * @param pQueueService queueService
     * @param pQueueBroadcaster queueBroadCaster
     */
    @Autowired
    public StateController(
            final ValidatorService pValidatorService,
            final SessionRepo pSessionRepo,
            final QueueService pQueueService,
            final QueueBroadcaster pQueueBroadcaster
         //   final SubscribeEventListener pSubscribeEventListener
    ) {
        this.validatorService = pValidatorService;
        this.sessionRepo = pSessionRepo;
        this.queueService = pQueueService;
        this.queueBroadcaster = pQueueBroadcaster;
    //    this.subscribeEventListener = pSubscribeEventListener;
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
        queueService.joinPool(req, payload);
        queueBroadcaster.broadcastQueue();
        return "OK, please request new state now.";
    }

    /** Kasitellaan subscribe-pyynto /queue/id/, joka tulee
     * kun hoitaja ottaa jonosta uuden chatin.
     *
     *  Toimenpiteet mita tehdaan:
     *  - Poistetaan jonosta kyseinen chatti.
     *  - Broadcastataan jonon uusi tila hoitajille
     *  - Kerrotaan kanavan osallisille, etta chatti on auki.
     * @param channelId channelId
     * @param accessor accessor
     * @return Joko tyhja String "" tai JSON {"content":"channel activated."}
     *          Palautusarvo kuljetetaan "jonotuskanavan" kautta jonottajalle
     *          seka hoitajalle tiedoksi, etta poppaus onnistui.
     * @throws Exception mika poikkeus
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

        /** Yritetaan popata. */
        String wakeUp = queueService.popQueue(channelId, accessor);
        if (wakeUp.isEmpty()) {
            /** Case: Toinen auth ehtikin popata taman ennen meita. */
            return "";
        }

        /** Broadcastataan jonon tila kaikille ammattilaisille.
         * HUOM: Ei broadcastata viesteja viela. Vasta kun joku subscribaa. */
        queueBroadcaster.broadcastQueue();

        /** Palautetaan poppaajalle tieto, etta poppaus onnistui. */
        return wakeUp;
    }




}
