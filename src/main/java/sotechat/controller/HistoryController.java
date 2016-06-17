package sotechat.controller;

import org.apache.xpath.operations.String;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Asus on 17.6.2016.
 */

@Controller
public class HistoryController {

    public HistoryController(){

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
    public final List<>
}
