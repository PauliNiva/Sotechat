package sotechat.controller;

import org.apache.xpath.operations.String;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Asus on 17.6.2016.
 */

@Controller
@RequestMapping('/proHistory')
public class HistoryController {

    public HistoryController(){

    }

    @RequestMapping(method = RequestMethod.GET)
    public final String showHistory(){
        return "forward:/chatHistory.html";
    }

    @RequestMapping(value = "/Conversation", method = RequestMethod.GET)
    public final String showConversation(){
        return "forward:/conversation.html";
    }
}
