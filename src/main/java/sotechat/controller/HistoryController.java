package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    }
}
