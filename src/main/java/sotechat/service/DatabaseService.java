package sotechat.service;

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
public class DatabaseService {

    PersonService personService;

    ConversationService conversationService;

    MessageService messageService;

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
     * @param session Http sessio
     * @throws Exception
     */
    private final void createConversation(String startMessage, String sender,
                                          HttpSession session)
            throws Exception{
        Message message = new Message(sender, startMessage, new Date());
        String channelId = get(session, "channelId");
        message.setChannelId(channelId);
        conversationService.addConversation(message, channelId);
        String category = get(session, "category");
        conversationService.setCategory(category, channelId);
    }

    /**
     * Lisätään parametrina annettuun sessioon liittyvä henkilö tietokannasta
     * session kanava id:n perusteella löytyvään keskusteluun ja lisataan tama
     * keskustelu henkilon keskusteluihin.
     * @param session Http sessio
     * @throws Exception
     */
    private final void addPersonToConversation(HttpSession session)
            throws Exception {
        String userId = get(session, "userId");
        String channelId = get(session, "channelId");
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
    private final void saveToDatabase(String username, String content,
                                      Date time, String channelId)
                                        throws Exception {
        Message message = new Message(username, content, time);
        message.setChannelId(channelId);
        messageService.addMessage(message);
        conversationService.addMessage(message, channelId);
    }

}
