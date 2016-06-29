package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

import sotechat.data.ChatLogger;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.service.ValidatorService;
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;

/**
 * Reititys ammattilaiskayttajan historian selaamiseen liittyville pyynnoille.
 */
@RestController
public class HistoryController {

    /**
     * Pyyntojen validointi.
     */
    private ValidatorService validatorService;

    /**
     * Viestien muistaminen.
     */
    private ChatLogger chatLogger;

    /**
     * Sessioiden kasittely.
     */
    private final SessionRepo sessionRepo;

    /**
     * Konstruktori.
     *
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

    /**
     * Client pyytaa tietyn kanavan vanhoja viesteja.
     *
     * @param pro Autentikaatiotiedot.
     * @param req Pyynto.
     * @param channelId Kanavatunnus.
     * @return Vanhat viestit, jos clientilla oikeus niihin.
     * Muuten <code>null</code>.
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
        return chatLogger.getLogs(channelId);
    }

    /**
     * Client pyytaa listauksen vanhoista keskusteluistaan.
     * @param professional Autentikointitiedot.
     * @param req Pyynto.
     * @return Vanhat keskustelut, jos client autentikoitinut.
     * Muuten <code>null</code>.
     */
    @RequestMapping(value = "/listMyConversations/", method = RequestMethod.GET)
    @ResponseBody
    public final List<ConvInfo> getConversations(
            final Principal professional,
            final HttpServletRequest req
    ) {
        if (professional == null) {
            /* Listaus on kaytossa vain autentikoituneille kayttajille. */
            return null;
        }
        String sessionId = req.getSession().getId();
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        String userId = session.get("userId");
        return chatLogger.getChannelsByUserId(userId);
    }

}
