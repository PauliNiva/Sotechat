package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
import sotechat.service.DatabaseService;
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;
import java.security.Principal;
import java.util.List;

/**
 * Created by Asus on 17.6.2016.
 */
/** Reititys ammattilaiskayttajan pyynnolle
 * "luettele kanavat, joilla olen ollut."
 * Vastaukseen saatuaan kayttaja voisi hakea yksittaisen kanavan lokit
 * lahettamalla normaalin WS subscriben kyseiselle kanavalle.
 */
@Controller
public class HistoryController {

    private DatabaseService databaseService;

    private final ChatLogger chatLogger;

    private final Mapper mapper;

    @Autowired
    public HistoryController(final DatabaseService dbservice,
                             ChatLogger pChatLogger,
                             Mapper pMapper){
        this.chatLogger = pChatLogger;
        this.mapper = pMapper;
        this.databaseService = dbservice;
    }

    @RequestMapping(value = "/messages/{channelId}", method = RequestMethod.GET)
    @ResponseBody
    public final List<MsgToClient> getMessages(@PathVariable("channelId")
                                                   final String channelId)
                                                    throws Exception {
        return databaseService.retrieveMessages(channelId);
    }

    @RequestMapping(value="/history/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public final List<ConvInfo> getConversations(@PathVariable("userId")
                                                     final String userId)
                                                    throws Exception {
        return databaseService.retrieveConversationInfo(userId);
    }

    @RequestMapping(value = "/getHistoricChannels", method = RequestMethod.GET)
    @ResponseBody
    public final String respondToHistoricChannelsRequest(
            final Principal professional
    ) throws Exception {
        if (professional == null) {
            return null;
        }
        String username = professional.getName();
        String userId = mapper.getIdFromRegisteredName(username);
        return chatLogger.getChannelsByUserId(userId);
    }
}
