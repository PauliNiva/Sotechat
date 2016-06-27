package sotechat.service;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;
import java.util.ArrayList;
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
    public DatabaseService(
            final PersonService personService,
            final ConversationService conversationService
    ) {
        this.personService = personService;
        this.conversationService = conversationService;
    }

    /**
     * Luodaan tietokantaan uusi keskustelu ja liitetään siihen aloitusviesti
     * sekä keskustelun kategoria.
     * @param sender aloitusviestin lahettaja
     * @param channelId kanavan id
     * @param category keskustelun kategoria
     */
    public final void createConversation(
            final String sender,
            final String channelId,
            final String category
    ) {
        try {
            DateTime time = new DateTime();
            Conversation conversation = new Conversation(channelId,
                    time.toString());
            conversationService.addConversation(conversation);
            conversationService.setCategory(category, channelId);
        } catch (Exception e) {
            System.out.println("DBE on createConversation! " + e.toString());
        }

    }

    /**
     * Luo uuden kayttajatilin ts. Person olion tietokantaan ja asettaa tälle
     * parametrina annetut nimimerkin, login-nimen, salasanan ja roolin sekä
     * käyttäjäid:n.
     * @param userId kayttajan id
     * @param loginName login-nimi
     * @param screenName niminerkki
     * @param role rooli
     * @param password salasana
     */
    public final void createNewUser(String userId, String loginName,
                                    String screenName, String role, String password){
        try {
            if(userId.isEmpty() || userId == null || loginName.isEmpty()
                    || loginName == null || password.isEmpty()
                    || password == null || screenName.isEmpty()
                    || screenName == null || role.isEmpty() || role == null)
                    throw new Exception();
            Person person = new Person(userId);
            person.setUserName(screenName);
            person.setLoginName(loginName);
            person.setRole(role);
            personService.addPerson(person, password);
        }catch (Exception e){
            System.out.println("DBE on createNewUser! " + e.toString());
        }
    }

    /**
     * Lisätään parametrina annetun kayttaja id:n omaava henkilo parametrina
     * annettua kanavaid:ta vastaavaan keskusteluun.
     * @param userId kayttajan id
     * @param channelId kanavan id
     */
    public final void addPersonToConversation(
            final String userId,
            final String channelId
    ) {
        try {
            Person person = personService.getPerson(userId);
            conversationService.addPerson(person, channelId);
            Conversation conv = conversationService.getConversation(channelId);
            personService.addConversation(userId, conv);
        } catch (Exception e) {
            System.out.println("DBE on addPersonToConversati! " + e.toString());
        }

    }

    /**
     * Tallennetaan viesti tietokantaan ja tietokannassa olevaan keskusteluun.
     * @param username viestin lähettäjän käyttäjänimi
     * @param content viestin sisältö
     * @param time viestin aikaleima
     * @param channelId viestin kanavan id
     */
    public final void saveMsg(
            final String username,
            final String content,
            final String time,
            final String channelId
    ) {
        try {
            Message message = new Message(username, content, time);
            Conversation conv = conversationService.getConversation(channelId);
            message.setChannelId(channelId);
            message.setConversation(conv);
            conversationService.addMessage(message, conv);
        } catch (Exception e) {
            System.out.println("DBE on saveMsg! " + e.toString());
        }

    }

    /**
     * Palauttaa parametrina annettua channelid:ta vastaavan keskustelun.
     * viestit aikaleiman mukaan jarjestettyna listana MsgToClient olioita.
     * @param channelId keskustelun kanavan id
     * @return List<MsgToClient> keskustelun viestit aikajarjestyksessa
     */
    public List<MsgToClient> retrieveMessages(
            final String channelId
    ) {
        try {
            Conversation conv = conversationService.getConversation(channelId);
            List<Message> messages = conv.getMessagesOfConversation();
            List<MsgToClient> messageList = new ArrayList<>();
            for (Message message : messages) {
                MsgToClient newMsg = wrapMessage(message);
                messageList.add(newMsg);
            }
            return messageList;
        } catch (Exception e) {
            System.out.println("DBE on retrieveMessages! " + e.toString());
            return new ArrayList<>();
        }

    }

    /** Palauttaa listan ConvInfo-olioita.
     * @param userId userId
     * @return lista convInfo-olioita
     */
    public final List<ConvInfo> getConvInfoListOfUserId(
            final String userId
    ) {
        try {
            Person person = personService.getPerson(userId);
            List<Conversation> convs = person.getConversationsOfPerson();
            List<ConvInfo> info = new ArrayList<>();
            for (Conversation conversation : convs) {
                info.add(wrapConversation(conversation));
            }
            return info;
        } catch (Exception e) {
            System.out.println("DBE on getConvInfoListOfUser! " + e.toString());
            return new ArrayList<>();
        }

    }

    /** Wraps conversation into a ConvInfo object.
     * @param conv conv
     * @return ConvInfo wrapper
     */
    private ConvInfo wrapConversation(
            final Conversation conv
    ) {
       String channelId = conv.getChannelId();
       String date = conv.getDate();
       /** ensimmainen viesti on asiakkaalta, joten tahan asiakkaan nimi */
        String person = "";
        if (conv.getMessagesOfConversation().size() > 0) {
            person = conv.getMessagesOfConversation().get(0).getSender();
        }
       String category = conv.getCategory();
       return new ConvInfo(channelId, date, person, category);
    }

    /**
     * Luo uuden MsgToClient olion parametrina annetun Message olion tietojen
     * pohjalta ts muuntaa Message olion MsgToClient olioksi.
     * @param message Message luokan ilmentyma
     * @return MsgToClient luokan ilmentyma
     */
    private MsgToClient wrapMessage(Message message) {
        String id = "" + message.getId();
        String name = message.getSender();
        String channelId = message.getChannelId();
        String time = message.getDate();
        String content = message.getContent();
        MsgToClient msg = new MsgToClient(id, name, channelId, time, content);
        return msg;
    }

    /** Tarkoitettu viestien poistamiseen tietokannasta demoamista varten.
     * @return Virheilmoitus tai tyhja String jos onnistui.
     */
    public String removeAllConversationsFromDatabase() {
        try {
            List<Conversation> conversations = conversationService.findAll();
            for (Conversation conversation : conversations) {
                /* Poistaa myos keskusteluun liitetyt viestit. */
                for (Person person : conversation.getParticipantsOfConversation()) {
                    person.removeConversation(conversation);
                }
                String id = conversation.getChannelId();
                conversationService.removeConversation(id);
            }
            return "";
        } catch (Exception e) {
            return "Virhe tietokannan tyhjennyksessa, muutoksia tietokantaan "
                    + "ei tehty. " + e.toString();
        }
    }

}
