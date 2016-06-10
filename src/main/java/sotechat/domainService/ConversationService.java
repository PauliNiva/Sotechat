package sotechat.domainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.PersonRepo;

import java.util.Date;

/**
 * Created by varkoi on 8.6.2016.
 */
@Service
public class ConversationService {

    /** Keskustelujen tallentamiseen */
    @Autowired
    private ConversationRepo conversationRepo;

    /** Henkiloiden tallentamiseen */
    @Autowired
    private PersonRepo personRepo;


    /**
     * Lisaa uuden keskustelun tietokantaan, jolle asetetaan aikaleima
     * ja tunnukseksi parametrina annettu kanavaid. Taman jalkeen lisataan
     * keskusteluun parametrina annettu viesti
     * @param message Message luokan olio, jossa kayttajan lahettama viesti
     * @param channelId keskustelun kanavan id
     */
    public boolean addConversation(Message message, String channelId)
            throws Exception {
            Conversation conv = new Conversation(new Date(), channelId);
            conversationRepo.save(conv);
            return addMessage(message, conv);
    }

    /**
     * Lisää parametrina annettua kanavaid:tä vastaavaan keskusteluun Person
     * luokan olion, joka haetaan repositoriosta parametrina annetun henkilön
     * id:n perusteella. Henkilo lisataan Keskustelun henkiloihin ja keskustelu
     * lisataan henkilon keskusteluihin.
     * @param personId osallistuvan henkilon id
     * @param channelId keskustelun kanavan id
     * @throws Exception IllegalArgumentException
     */
    public void addPerson(String personId, String channelId)
            throws Exception {
                Conversation conv = conversationRepo.findOne(channelId);
                Person person = personRepo.findOne(personId);
                addConnection(person, conv);
    }

    /**
     * Liittaa parametrina annettulla kanavaid:lla repositoriosta loytyvan
     * keskustelun kategoriaksi parametrina annetun aihealueen
     * @param category keskustelun aihealue
     * @param channelId keskustelun kanavan id
     * @throws Exception IllegalArgumentException
     */
    public void setCategory(String category, String channelId) throws Exception {
            Conversation conv = conversationRepo.findOne(channelId);
            conv.setCategory(category);
            conversationRepo.save(conv);
    }

    /**
     * Lisaa viestin keskusteluun, eli liittaa parametrina annetun Message
     * -olion parametrina annetun kanavaid:n perusteella repositoriosta
     * loytyvan Convertasion -olion listaan.
     * @param message Message luokan olio, jossa kayttajan lahettama viesti
     * @param ChannelId keskustelun kanavan id
     * @return true, jos vietsin lisaaminen tietokantaan onnistui, false jos ei
     * @throws Exception IllegalArgumentException
     */
    public void addMessage(Message message, String ChannelId)
            throws Exception {
            Conversation conv = conversationRepo.findOne(ChannelId);
            addMessage(message, conv);
    }

    /**
     * Lisaa parametrina annetun Message -luokan olion parametrina annetun
     * Conversation -olion listaan, ts liittaa viestin keskusteluun.
     * @param message Message -luokan olio, jossa on kayttajan viesti
     * @param conv Conversation -luokan oli, joka edustaa keskustelua, johon
     *             viesti liittyy
     * @throws Exception NullPointerException
     */
    private void addMessage(Message message, Conversation conv)
            throws Exception {
            conv.addMessageToConversation(message);
            conversationRepo.save(conv);
    }

    /**
     * Liittaa Person luokan olion Conversation luokan olion listaan ja
     * Conversation luokan olion Person luokan olion listaan.
     * @param person 
     * @param conversation
     * @throws Exception
     */
    private void addConnection(Person person, Conversation conversation) throws Exception {
        conversation.addPersonToConversation(person);
        person.addConversationToPerson(conversation);
        conversationRepo.save(conversation);
        personRepo.save(person);
        }
    }

    public void delete(String channelId) throws Exception {
        conversationRepo.delete(channelId);
    }
}
