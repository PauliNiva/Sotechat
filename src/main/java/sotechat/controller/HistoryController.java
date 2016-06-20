package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sotechat.data.ChatLogger;
import sotechat.data.Mapper;

import java.security.Principal;

/** Reititys ammattilaiskayttajan pyynnolle
 *  "luettele kanavat, joilla olen ollut.".
 * Vastaukseen saatuaan kayttaja voisi hakea yksittaisen kanavan lokit
 * lahettamalla normaalin WS subscriben kyseiselle kanavalle.
 */
@RestController
public class HistoryController {

    /** Chat Logger. */
    private final ChatLogger chatLogger;

    /** Mapper. */
    private final Mapper mapper;

    /**
     * Konstruktori.
     * @param pChatLogger chat logger
     * @param pMapper mapper
     */
    @Autowired
    public HistoryController(
            final ChatLogger pChatLogger,
            final Mapper pMapper
    ) {
        this.chatLogger = pChatLogger;
        this.mapper = pMapper;
    }

    /** Reititys ammattilaiskayttajan pyynnolle
     *  "luettele kanavat, joilla olen ollut.".
     * @param professional kirjautumistiedot
     * @return vastaus pyyntoon, null jos ei kirjautunut
     */
    @RequestMapping(value = "/getHistoricChannels", method = RequestMethod.GET)
    public final String respondToHistoricChannelsRequest(
            final Principal professional
    ) {
        if (professional == null) {
            return null;
        }
        String username = professional.getName();
        String userId = mapper.getIdFromRegisteredName(username);
        return chatLogger.getChannelsByUserId(userId);
    }
}
