package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sotechat.service.DatabaseService;
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;

import java.util.List;

/**
 * Created by Asus on 17.6.2016.
 */

@Controller
public class HistoryController {

    private DatabaseService databaseService;

    @Autowired
    public HistoryController(final DatabaseService dbservice){
        databaseService = dbservice;
    }

    @RequestMapping(value = "/proHistory")
    public final String showHistory(){
        return "forward: chatHistory.html";
    }

    @RequestMapping(value = "/Conversation")
    public final String showConversation(){
        return "forward: conversation.html";
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
=======
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
>>>>>>> 514ff2e71f6144434eae357efa90e4304af0b3c9
    }
}
