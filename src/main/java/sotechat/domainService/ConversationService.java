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
 * Luokka tietokannassa olevien keskustelujen hallinnoimiseen
 * (CRUD -operaatiot)
 * Created by varkoi on 8.6.2016.
 */
@Service
public class ConversationService {

    /** Keskustelujen tallentamiseen */
    private ConversationRepo conversationRepo;

    /** Henkiloiden tallentamiseen */
    private PersonRepo personRepo;

    @Autowired
    /** Konstruktorissa injektoidaan ConversationRepo ja Personrepo */
    public ConversationService(ConversationRepo conversationRepo,
                               PersonRepo personRepo) {
        this.conversationRepo = conversationRepo;
        this.personRepo = personRepo;
    }

    /**
     * Lisaa uuden keskustelun tietokantaan, jolle asetetaan aikaleima
     * ja tunnukseksi parametrina annettu kanavaid. Taman jalkeen lisataan
     * keskusteluun parametrina annettu viesti
     * @param message Message luokan olio, jossa kayttajan lahettama viesti
     * @param channelId keskustelun kanavan id
     */
    public void addConversation(Message message, String channelId)
            throws Exception {
            Conversation conv = new Conversation(new Date(), channelId);
            conversationRepo.save(conv);
            addMessage(message, conv);
    }

    /**
     * Lisää parametrina annettua kanavaid:tä vastaavaan keskusteluun Person
     * luokan olion, joka haetaan tietokannasta parametrina annetun henkilön
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
     * Liittaa parametrina annettulla kanavaid:lla tietokannasta loytyvan
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
     * -olion parametrina annetun kanavaid:n perusteella tietokannasta
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
     * Luo yhteyden keskustelun ja henkilon valille. Liittaa parametrina
     * annetun Person luokan olion parametrina annetun Conversation luokan
     * olion listaan ja parametrina annetun Conversation luokan olion
     * parametrina annetun Person luokan olion listaan.
     * @param person Person luokan olio, joka edustaa ammattilaista, joka on
     *               ottanut keskustelun jonosta.
     * @param conversation Conversation luokan olio ts. keskustelu johon
     *                     henkilo liitetaan.
     * @throws Exception NullPointerException
     */
    private void addConnection(Person person, Conversation conversation) throws Exception {
        conversation.addPersonToConversation(person);
        person.addConversationToPerson(conversation);
        conversationRepo.save(conversation);
        personRepo.save(person);
        }
    }

    /**
     * Poistaa keskustelun tietokannasta ts. poistaa parametrina annettua
     * kanavaid:ta vastaavaa keskustelua edustavan Conversation luokan olion
     * tietokannasta.
     * @param channelId keskustelun kanavan id
     * @throws Exception IllegalArgumentException
     */
    public void delete(String channelId) throws Exception {
        conversationRepo.delete(channelId);
    }
}
