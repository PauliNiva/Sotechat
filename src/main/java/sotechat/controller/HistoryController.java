package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.service.DatabaseService;
import sotechat.service.ValidatorService;
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

/** Reititys ammattilaiskayttajan historian selaamiseen liittyville pyynnoille:
 *  - "luettele kanavat, joilla olen ollut."
 *  - "anna kanavan x lokit".
 */
@RestController
public class HistoryController {

    /** Validator Service. */
    private ValidatorService validatorService;

    /** Chat Logger. */
    private ChatLogger chatLogger;

    /** Session Repo. */
    private final SessionRepo sessionRepo;

    /**
     * Konstruktori.
     * @param pValidatorService p
     * @param pChatLogger p
     * @param pSessionRepo p
     */
    @Autowired
    public HistoryController(
            final ValidatorService pValidatorService,
            final ChatLogger pChatLogger,
            final SessionRepo pSessionRepo
    ) {
        this.validatorService = pValidatorService;
        this.chatLogger = pChatLogger;
        this.sessionRepo = pSessionRepo;
    }

    /** Client pyytaa meilta tietyn kanavan lokeja.
     * @param pro autentikaatiotiedot
     * @param req req
     * @param channelId channelId
     * @return lokit, jos clientilla oikeus niihin. Muuten null.
     */
    @RequestMapping(value = "/getLogs/{channelId}", method = RequestMethod.GET)
    @ResponseBody
    public final List<MsgToClient> getMessages(
            final @PathVariable("channelId") String channelId,
            final HttpServletRequest req,
            final Principal pro
    ) {
        String error = validatorService.validateLogRequest(pro, req, channelId);
        if (!error.isEmpty()) {
            System.out.println("Hacking attempt with getLogs? " + error);
            return null;
        }
        System.out.println("Retrieving channel " + channelId + " ##########");
        return chatLogger.getLogs(channelId);
    }

    /** Client pyytaa listauksen vanhoista keskusteluistaan.
     * @param professional autentikointitiedot
     * @return listaus, jos client autentikoitinut. Muuten null.
     * @throws Exception
     */
    @RequestMapping(value = "/listMyConversations/", method = RequestMethod.GET)
    @ResponseBody
    public final List<ConvInfo> getConversations(
            final Principal professional,
            final HttpServletRequest req
    ) {
        if (professional == null) {
            /** Listaus on kaytossa vain autentikoituneille kayttajille. */
            return null;
        }
        System.out.println("REQUESTING HISTORY ##########");
        String sessionId = req.getSession().getId();
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        String userId = session.get("userId");
        return chatLogger.getChannelsByUserId(userId);
    }

}
