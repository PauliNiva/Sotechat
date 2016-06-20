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

/** Reititys ammattilaiskayttajan pyynnolle
 *  "luettele kanavat, joilla olen ollut.".
 * Vastaukseen saatuaan kayttaja voisi hakea yksittaisen kanavan lokit
 * lahettamalla normaalin WS subscriben kyseiselle kanavalle.
 */
@Controller
public class HistoryController {

    private DatabaseService databaseService;

    /** Mapper. */
    private final Mapper mapper;

    /**
     * Konstruktori.
     * @param dbservice databaseService
     * @param pMapper mapper
     */
    @Autowired
    public HistoryController(final DatabaseService dbservice,
                             Mapper pMapper){
        this.mapper = pMapper;
        this.databaseService = dbservice;
    }

    @RequestMapping(value = "/messages/{channelId}",
            method = RequestMethod.GET)
    @ResponseBody
    public final List<MsgToClient> getMessages(@PathVariable("channelId")
                                                   final String channelId)
                                                    throws Exception {
        System.out.println("Retrieving channel " + channelId + " ##########");
        return databaseService.retrieveMessages(channelId);
    }

    @RequestMapping(value="/history", method = RequestMethod.GET)
    @ResponseBody
    public final List<ConvInfo> getConversations(final Principal professional)
                                                    throws Exception {
        if (professional == null) {
            return null;
        }
        System.out.println("REQUESTING HISTORY ##########");
        String username = professional.getName();
        String userId = mapper.getIdFromRegisteredName(username);
        return databaseService.retrieveConversationInfo(userId);
    }

}
