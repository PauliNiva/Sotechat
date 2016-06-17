package sotechat.service;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.domainService.ConversationService;
import sotechat.domainService.PersonService;
import sotechat.wrappers.MsgToClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Luokka tietokantaoperaatioiden toteuttamiseen
 */
@Service
public class DatabaseService {

    /** Henkiloihin liittyvat palvelut */
    private PersonService personService;

    /** Keskusteluihin liittyvat palvelut */
    private ConversationService conversationService;

    /** Viesteihin liittyvat palvelut */
    @Autowired
    public DatabaseService(PersonService personService,
                           ConversationService conversationService) {
        this.personService = personService;
        this.conversationService = conversationService;
    }

    /**
     * Luodaan tietokantaan uusi keskustelu ja liitetään siihen aloitusviesti
     * sekä keskustelun kategoria.
     * @param sender aloitusviestin lahettaja
     * @param channelId kanavan id
     * @param category keskustelun kategoria
     * @throws Exception
     */
    public final void createConversation(final String sender,
        final String channelId, final String category) throws Exception {
            DateTime time = new DateTime();
            Conversation conversation = new Conversation(channelId,
                    time.toString());
            conversationService.addConversation(conversation);
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
    public final void saveMsgToDatabase(String username, String content,
                                        String time, String channelId)
                                        throws Exception {
        Message message = new Message(username, content, time);
        Conversation conv = conversationService.getConversation(channelId);
        message.setChannelId(channelId);
        message.setConversation(conv);
        conversationService.addMessage(message, conv);
    }

    /**
     * Palauttaa listan henkiloon liittyvista channelid:sta eli henkilon
     * kaikkien keskustelujen channelid:t listana.
     * @param userId henkilon id
     * @return List<String> henkiloon liittyvat channelid:t
     * @throws Exception IllegalArgumentException
     */
    public final List<String> personsConversations(String userId)
            throws Exception {
        Person person = personService.getPerson(userId);
        List<Conversation> convs = person.getConversationsOfPerson();
        List<String> channelIds = new ArrayList<String>();
        for(Conversation conv : convs){
            channelIds.add(conv.getChannelId());
        }
        return channelIds;
    }

    /**
     * Palauttaa parametrina annettua channelid:ta vastaavan keskustelun
     * viestit aikaleiman mukaan jarjestettyna listana MsgToClient olioita.
     * @param channelId keskustelun kanavan id
     * @return List<MsgToClient> keskustelun viestit aikajarjestyksessa
     */
    public final List<MsgToClient> retrieveMessages(String channelId)
            throws Exception {
        Conversation conv = conversationService.getConversation(channelId);
        List<Message> messages = conv.getMessagesOfConversation();
        List<MsgToClient> messagelist = new ArrayList<MsgToClient>();
        for(Message message : messages){
            MsgToClient newMsg = wrap(message);
            messagelist.add(newMsg);
        }
        Collections.sort(messagelist);
        return messagelist;
    }

    /**
     * Luo uuden MsgToClient olion parametrina annetun Message olion tietojen
     * pohjalta ts muuntaa Message olion MsgToClient olioksi.
     * @param message Message luokan ilmentyma
     * @return MsgToClient luokan ilmentyma
     */
    private final MsgToClient wrap(Message message) throws Exception {
        String name = message.getSender();
        String channelId = message.getChannelId();
        String time = message.getDate();
        String content = message.getContent();
        MsgToClient msg = new MsgToClient(name, channelId, time, content);
        return msg;
    }

}
