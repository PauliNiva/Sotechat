package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.domainService.ConversationService;
import sotechat.domainService.MessageService;
import sotechat.domainService.PersonService;

import javax.servlet.http.HttpSession;
import java.util.Date;

import static sotechat.util.Utils.get;

/**
 * Created by varkoi on 13.6.2016.
 */
@Service
public class DatabaseService {

    PersonService personService;

    ConversationService conversationService;

    MessageService messageService;

    @Autowired
    public DatabaseService(PersonService personService,
                           ConversationService conversationService,
                           MessageService messageService){
        this.personService = personService;
        this.conversationService = conversationService;
        this.messageService = messageService;
    }

    /**
     * Luodaan tietokantaan uusi keskustelu ja liitetään siihen aloitusviesti
     * sekä keskustelun kategoria.
     * @param startMessage aloitusviestin sisalto
     * @param sender aloitusviestin lahettaja
     * @param channelId kanavan id
     * @param category keskustelun kategoria
     * @throws Exception
     */
    public final void createConversation(String startMessage, String sender,
                                          String channelId, String category)
            throws Exception{
        Message message = new Message(sender, startMessage, new Date());
        message.setChannelId(channelId);
        conversationService.addConversation(message, channelId);
        conversationService.setCategory(category, channelId);
    }

    /**
     * Lisätään parametrina annetun kayttaja id:n omaava henkilo parametrina
     * annettua kanavaid:ta vastaavaan keskusteluun.
     * @param userId kayttajan id
     * @param channelId kanavan id
     * @throws Exception
     */
    public final void addPersonToConversation(String userId, String channelId)
            throws Exception {
        Person person = personService.getPerson(userId);
        conversationService.addPerson(person, channelId);
        Conversation conv = conversationService.getConversation(channelId);
        personService.addConversation(userId, conv);
    }

    /**
     * Tallennetaan viesti tietokantaan ja tietokannassa olevaan keskusteluun.
     * @param username viestin lähettäjän käyttäjänimi
     * @param content viestin sisältö
     * @param time viestin aikaleima
     * @param channelId viestin kanavan id
     * @throws Exception
     */
    public final void saveToDatabase(String username, String content,
                                      Date time, String channelId)
                                        throws Exception {
        Message message = new Message(username, content, time);
        message.setChannelId(channelId);
        messageService.addMessage(message);
        conversationService.addMessage(message, channelId);
    }

}
