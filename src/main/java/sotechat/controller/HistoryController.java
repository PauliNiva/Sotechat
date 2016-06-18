package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sotechat.data.ChatLogger;
import sotechat.data.Mapper;

import java.security.Principal;

/** Reititys ammattilaiskayttajan pyynnolle
 * "luettele kanavat, joilla olen ollut."
 * Vastaukseen saatuaan kayttaja voisi hakea yksittaisen kanavan lokit
 * lahettamalla normaalin WS subscriben kyseiselle kanavalle.
 */
@RestController
public class HistoryController {

    private final ChatLogger chatLogger;

    private final Mapper mapper;

    @Autowired
    public HistoryController(
            ChatLogger pChatLogger,
            Mapper pMapper
    ) {
        this.chatLogger = pChatLogger;
        this.mapper = pMapper;
    }

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
