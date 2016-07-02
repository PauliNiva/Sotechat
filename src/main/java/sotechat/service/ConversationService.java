package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;

/**
 * Luokka tietokannassa olevien keskustelujen hallinnoimiseen.
 * (CRUD -operaatiot)
 */
@Service
public class ConversationService {

    /**
     * JPA-repositorio, josta voidaan hakea tai jonne voidaan tallettaa
     * Message-luokan olioita.
     */
    @Autowired
    private MessageRepo messageRepo;

    /**
     * JPA-repositorio, josta voidaan hakea tai jonne voidaan tallettaa
     * Conversation-luokan olioita.
     */
    @Autowired
    private ConversationRepo conversationRepo;

    /**
     * Lisaa uuden keskustelun tietokantaan, jolle asetetaan aikaleima
     * ja tunnukseksi parametrina annettu kanavaid. Taman jalkeen lisataan
     * keskusteluun parametrina annettu viesti.
     *
     * @param conv lisattava keskustelu
     * @throws Exception Poikkeus, joka heitetaan jos tietokantaan tallettaminen
     * epaonnistuu.
     */
    @Transactional
    public void addConversation(final Conversation conv)
            throws Exception {
        conversationRepo.save(conv);
    }

    /**
     * Lisaa parametrina annettua kanavaid:ta vastaavaan keskusteluun
     * parametrina annetun Person luokan olion. Henkilo lisataan Keskustelun
     * henkiloihin.
     *
     * @param person Person luokan oli, joka halutaan lisata keskusteluun
     * @param channelId keskustelun kanavan id
     * @throws Exception Poikkeus, joka heitetaan, jos tietokantaan
     * tallettaminen epaonnistuu.
     */
    @Transactional
    public void addPerson(final Person person, final String channelId)
            throws Exception {
        Conversation conv = conversationRepo.findOne(channelId);
        conv.addPersonToConversation(person);
        conversationRepo.save(conv);
    }

    /**
     * Liittaa parametrina annettulla kanavaid:lla tietokannasta loytyvan
     * keskustelun kategoriaksi parametrina annetun aihealueen.
     *
     * @param category keskustelun aihealue
     * @param channelId keskustelun kanavan id
     * @throws Exception Poikkeus, joka heitetaan, jos tietokantaan
     * tallettaminen epaonnistuu.
     */
    @Transactional
    public void setCategory(final String category, final String channelId)
            throws Exception {
        Conversation conv = conversationRepo.findOne(channelId);
        conv.setCategory(category);
        conversationRepo.save(conv);
    }

    /**
     * Lisaa parametrina annetun Message-luokan olion parametrina annetun
     * Conversation-olion listaan, ts. liittaa viestin keskusteluun.
     * MessageRepoa tarvitaan, jotta viesti saadaan talletettua tietokantaan
     * ensin, ja kun viesti lisataan keskusteluun, viestia ei lisata kahteen
     * kertaan.
     *
     * @param message Message-luokan olio, jossa on kayttajan viesti
     * @param conv Conversation-luokan oli, joka edustaa keskustelua, johon
     *             viesti liittyy
     * @return Palauttaa keskusteluun lisatyn viestin Message-oliona.
     * @throws Exception Poikkeus, joka heitetaan, jos tietokantaan
     * tallettaminen epaonnistuu.
     */
    @Transactional
    public Message addMessage(final Message message, final Conversation conv)
            throws Exception {
        message.setConversation(conv);
        Message messageToBeAddedToConversation = messageRepo.save(message);
        conv.addMessageToConversation(messageToBeAddedToConversation);
        conversationRepo.save(conv);
        return messageToBeAddedToConversation;
    }

    /**
     * Poistaa keskustelusta viestin.
     *
     * @param message Viesti joka halutaan poistaa (taytyy etsia ensin
     *                messageServicesta)
     */
    @Transactional
    public void removeMessage(final Message message) {
        String channelId = message.getConversation().getChannelId();
        Conversation conv = conversationRepo.findOne(channelId);
        conv.getMessagesOfConversation().remove(message);
        conversationRepo.save(conv);
    }

    /**
     * Poistaa keskustelun tietokannasta.
     *
     * @param channelId keskustelun kanavan id
     * @throws Exception Poikkeus, joka heitetaan, jos tietokantaan
     * tallettaminen epaonnistuu.
     */
    @Transactional
    public void removeConversation(final String channelId) throws Exception {
        conversationRepo.delete(channelId);
    }

    /**
     * Palauttaa parametrina annettua channelid:ta vastaavan keskustelun.
     *
     * @param channelId haetun keskustelun kanavaid
     * @return Conversation olio, jolla pyydetty kanavaid
     * @throws Exception Poikkeus, joka heitetaan, jos tietokantaan
     * tallettaminen epaonnistuu.
     */
    @Transactional
    public Conversation getConversation(final String channelId)
            throws Exception {
        return conversationRepo.findOne(channelId);
    }

    /**
     * Palauttaa listan kaikista tietokannan keskusteluista.
     *
     * @return lista Conversation-olioista
     */
    @Transactional
    public List<Conversation> findAll() {
        return conversationRepo.findAll();
    }

}
