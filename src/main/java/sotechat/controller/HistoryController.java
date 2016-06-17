package sotechat.controller;

import org.apache.xpath.operations.String;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sotechat.service.DatabaseService;
import sotechat.wrappers.MsgToClient;

/**
 * Created by Asus on 17.6.2016.
 */

@Controller
public class HistoryController {

    private final DatabaseService dbservice;

    @Autowired
    public HistoryController(final DatabaseService dbservice){
        DatabaseService dbservice = dbservice;
    }

    @RequestMapping(value = "/proHistory" method = RequestMethod.GET)
    public final String showHistory(){
        return "forward:/chatHistory.html";
    }

    @RequestMapping(value = "/Conversation", method = RequestMethod.GET)
    public final String showConversation(){
        return "forward:/conversation.html";
    }

    @RequestMapping(value = "/messages/{channelId}" method = RequestMethod.GET)
    @ResponseBody
    public final List<MsgToClient> getMessages(@PathVariable("channelId") final String channelId){
        return dbservice.retrieveMessages(channelId);
    }
}
