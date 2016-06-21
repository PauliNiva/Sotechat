package sotechat.domainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;

/**
 * Luokka tietokannassa olevien keskustelujen hallinnoimiseen
 * (CRUD -operaatiot)
 */
@Service
public class ConversationService {

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private ConversationRepo conversationRepo;

    /**
     * Lisaa uuden keskustelun tietokantaan, jolle asetetaan aikaleima
     * ja tunnukseksi parametrina annettu kanavaid. Taman jalkeen lisataan
     * keskusteluun parametrina annettu viesti
     * @param conv lisättävä keskustelu
     */
    @Transactional
    public void addConversation(Conversation conv)
            throws Exception {
            conversationRepo.save(conv);
    }

    /**
     * Lisää parametrina annettua kanavaid:tä vastaavaan keskusteluun
     * parametrina annetun Person luokan olion. Henkilo lisataan Keskustelun
     * henkiloihin.
     * @param person Person luokan oli, joka halutaan lisata keskusteluun
     * @param channelId keskustelun kanavan id
     * @throws Exception IllegalArgumentException
     */
    @Transactional
    public void addPerson(Person person, String channelId)
            throws Exception {
                Conversation conv = conversationRepo.findOne(channelId);
                conv.addPersonToConversation(person);
                conversationRepo.save(conv);
    }

    /**
     * Liittaa parametrina annettulla kanavaid:lla tietokannasta loytyvan
     * keskustelun kategoriaksi parametrina annetun aihealueen
     * @param category keskustelun aihealue
     * @param channelId keskustelun kanavan id
     * @throws Exception IllegalArgumentException
     */
    @Transactional
    public void setCategory(String category, String channelId) throws Exception {
            Conversation conv = conversationRepo.findOne(channelId);
            conv.setCategory(category);
            conversationRepo.save(conv);
    }

    /**
     * Lisaa parametrina annetun Message -luokan olion parametrina annetun
     * Conversation -olion listaan, ts liittaa viestin keskusteluun.
     * MessageRepoa tarvitaan, jotta viesti saadaan talletettua tietokantaan
     * ensin, ja kun viesti lisätään keskusteluun, viestiä ei lisätä kahteen
     * kertaan.
     * @param message Message -luokan olio, jossa on kayttajan viesti
     * @param conv Conversation -luokan oli, joka edustaa keskustelua, johon
     *             viesti liittyy
     * @throws Exception NullPointerException
     */

    @Transactional
    public Message addMessage(Message message, Conversation conv)
            throws Exception {
            message.setConversation(conv);
            Message messageToBeAddedToConversation = messageRepo.save(message);
            conv.addMessageToConversation(messageToBeAddedToConversation);
            conversationRepo.save(conv);
            return messageToBeAddedToConversation;
    }

    /**
     * Lisaa parametrina annetun Message olion keskusteluun, joka etsitaan
     * tietokannasta parametrina annetun kanava id:n perusteella.
     * MessageRepoa tarvitaan, jotta viesti saadaan talletettua tietokantaan
     * ensin, ja kun viesti lisätään keskusteluun, viestiä ei lisätä kahteen
     * kertaan.
     * @param message Message lisattava viesti
     * @param channelId kanavaid jonka osoittamaan keskusteluun viesti halutaan
     *                  lisata
     */
    /*
    @Transactional
    public Message addMessage(Message message, String channelId) {
        Conversation conv = conversationRepo.findOne(channelId);
        message.setConversation(conv);
        Message messageToBeAddedToConversation = messageRepo.save(message);
        conv.addMessageToConversation(message);
        conversationRepo.save(conv);
        return messageToBeAddedToConversation;
    }
    */

    /**
     * Poistaa keskustelusta viestin ts. poistaa parametrina annetun Message
     * olion sen muuttujista loytyvan Conversation olion listasta ja paivittaa
     * muutoksen tietokantaan.
     * @param message Viesti joka halutaan poistaa (taytyy etsia ensin
     *                messageServicesta)
     */
    @Transactional
    public void removeMessage(Message message) {
        String channelId = message.getConversation().getChannelId();
        Conversation conv = conversationRepo.findOne(channelId);
        conv.getMessagesOfConversation().remove(message);
        conversationRepo.save(conv);
    }

    /**
     * Poistaa keskustelun tietokannasta ts. poistaa parametrina annettua
     * kanavaid:ta vastaavaa keskustelua edustavan Conversation luokan olion
     * tietokannasta.
     * @param channelId keskustelun kanavan id
     * @throws Exception IllegalArgumentException
     */
    @Transactional
    public void removeConversation(String channelId) throws Exception {
        conversationRepo.delete(channelId);
    }

    /**
     * Palauttaa parametrina annettua channel id:tä vastaavan keskustelun
     * @param channelId haetun keskustelun kanavaid
     * @return Conversation olio, jolla pyydetty kanavaid
     */
    @Transactional
    public Conversation getConversation(String channelId) throws Exception {
        return conversationRepo.findOne(channelId);
    }

}
