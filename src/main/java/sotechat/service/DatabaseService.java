package sotechat.service;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.domainService.ConversationService;
import sotechat.domainService.PersonService;
import sotechat.wrappers.ConvInfo;
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
        final String channelId, final String category) {
        try {
            DateTime time = new DateTime();
            Conversation conversation = new Conversation(channelId,
                    time.toString());
            conversationService.addConversation(conversation);
            conversationService.setCategory(category, channelId);
        } catch (Exception e) {

        }

    }

    /**
     * Lisätään parametrina annetun kayttaja id:n omaava henkilo parametrina
     * annettua kanavaid:ta vastaavaan keskusteluun.
     * @param userId kayttajan id
     * @param channelId kanavan id
     * @throws Exception
     */
    public final void addPersonToConversation(String userId, String channelId)
            {
                try {
                    Person person = personService.getPerson(userId);
                    conversationService.addPerson(person, channelId);
                    Conversation conv = conversationService.getConversation(channelId);
                    personService.addConversation(userId, conv);
                } catch (Exception e) {

                }

    }

    /**
     * Tallennetaan viesti tietokantaan ja tietokannassa olevaan keskusteluun.
     * @param username viestin lähettäjän käyttäjänimi
     * @param content viestin sisältö
     * @param time viestin aikaleima
     * @param channelId viestin kanavan id
     * @throws Exception
     */
    public final void saveMsg(String username, String content,
                              String time, String channelId) {
        try {
            Message message = new Message(username, content, time);
            Conversation conv = conversationService.getConversation(channelId);
            message.setChannelId(channelId);
            message.setConversation(conv);
            conversationService.addMessage(message, conv);
        } catch (Exception e) {

        }

    }

    /**
     * Palauttaa listan henkiloon liittyvista channelid:sta eli henkilon
     * kaikkien keskustelujen channelid:t listana.
     * @param userId henkilon id
     * @return List<String> henkiloon liittyvat channelid:t
     * @throws Exception IllegalArgumentException
     */
    public final List<String> personsConversations(String userId)
            {
                try {
                    Person person = personService.getPerson(userId);
                    List<Conversation> convs = person.getConversationsOfPerson();
                    List<String> channelIds = new ArrayList<String>();
                    for(Conversation conv : convs){
                        channelIds.add(conv.getChannelId());
                    }
                    return channelIds;
                } catch (Exception e) {
                    return new ArrayList<>();
                }

    }

    /**
     * Palauttaa parametrina annettua channelid:ta vastaavan keskustelun
     * viestit aikaleiman mukaan jarjestettyna listana MsgToClient olioita.
     * @param channelId keskustelun kanavan id
     * @return List<MsgToClient> keskustelun viestit aikajarjestyksessa
     */
    public final List<MsgToClient> retrieveMessages(String channelId) {
        try {
            Conversation conv = conversationService.getConversation(channelId);
            List<Message> messages = conv.getMessagesOfConversation();
            List<MsgToClient> messagelist = new ArrayList<MsgToClient>();
            for(Message message : messages){
                MsgToClient newMsg = wrapMessage(message);
                messagelist.add(newMsg);
            }
            return messagelist;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    public final List<ConvInfo> retrieveConversationInfo(String userId)
            {
                try {
                    Person person = personService.getPerson(userId);
                    List<Conversation> convs = person.getConversationsOfPerson();
                    List<ConvInfo> info = new ArrayList<ConvInfo>();
                    for (Conversation conversation : convs){
                        info.add(wrapConversation(conversation));
                    }
                    return info;
                } catch (Exception e) {
                    return new ArrayList<>();
                }

    }

    private final ConvInfo wrapConversation(Conversation conv)
           {
               try {
                   String channelId = conv.getChannelId();
                   String date = conv.getDate();
                   /** ensimmainen viesti on asiakkaalta, joten tahan asiakkaan nimi */
                   String person = conv.getMessagesOfConversation().get(0).getSender();
                   String category = conv.getCategory();
                   return new ConvInfo(channelId, date, person, category);
               } catch (Exception e) {
                    return null;
               }

    }

    /**
     * Luo uuden MsgToClient olion parametrina annetun Message olion tietojen
     * pohjalta ts muuntaa Message olion MsgToClient olioksi.
     * @param message Message luokan ilmentyma
     * @return MsgToClient luokan ilmentyma
     */
    private final MsgToClient wrapMessage(Message message) {
        String id = "" + message.getId();
        String name = message.getSender();
        String channelId = message.getChannelId();
        String time = message.getDate();
        String content = message.getContent();
        MsgToClient msg = new MsgToClient(id, name, channelId, time, content);
        return msg;
    }

}
