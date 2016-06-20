package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
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

    /** Mapper. */
    private final Mapper mapper;

    /**
     * Konstruktori.
     * @param pChatLogger p
     * @param pMapper p
     */
    @Autowired
    public HistoryController(
            final ValidatorService pValidatorService,
            final ChatLogger pChatLogger,
            final Mapper pMapper
    ) {
        this.validatorService = pValidatorService;
        this.chatLogger = pChatLogger;
        this.mapper = pMapper;
    }

    /** Client pyytaa meilta tietyn kanavan lokeja.
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
    public final List<ConvInfo> getConversations(final Principal professional)
                                                    throws Exception {
        if (professional == null) {
            return null;
        }
        System.out.println("REQUESTING HISTORY ##########");
        String username = professional.getName();
        String userId = mapper.getIdFromRegisteredName(username);
        return chatLogger.getChannelsByUserId(userId);
    }

}
