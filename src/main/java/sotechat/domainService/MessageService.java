package sotechat.domainService;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Message;
import sotechat.repo.MessageRepo;

/**
 * Luokka tietokannassa olevien Message -olioiden tallentamiseen
 * (CRUD -operaatiot)
 */
@Service
public class MessageService  {

    /** repositorio */
    @Autowired
    private MessageRepo messageRepo;

    /**
     * Tallentaa Message olion eli viestin tietokantaan
     * @param message Message olio joka sisaltaa lahetetyn viestin tiedot
     * @throws Exception
     */
    @Transactional
    public Message addMessage(Message message) throws Exception {
        return messageRepo.save(message);
    }

    /**
     * Hakee parametrina annettua id:ta vastaavan viestin tietokannasta
     * @param messageId viestin id
     * @return Message olio joka vastaa etsittya viestia
     * @throws Exception IllegalArgumentException
     */
    @Transactional
    public Message getMessage(Long messageId) throws Exception {
        return messageRepo.findOne(messageId);
    }

    /**
     * Poistaa parametrina annettua id:ta vastaavan viestin tietokannasta
     * @param messageId viestin id
     * @throws Exception IllegalArgumentException
     */
    @Transactional
    public void removeMessage(Long messageId) throws Exception {
        messageRepo.delete(messageId);
    }

    /**
     * Palauttaa listan parametrina annettua id:ta vastaavan keskustelun
     * viesteista
     * @param channelId keskustelun kanavaid
     * @return Lista Message oliota, jotka liittyvat haettuun Conversation olioon
     * @throws Exception
     */
    @Transactional
    public List<Message> messagesOfConversation(String channelId) throws Exception {
        return messageRepo.findByChannelId(channelId);
    }

    /**
     * Poistaa tietokannasta kaikki keskustelun viestit ts.kanavaid:hen
     * liitetyt Message oliot
     * @param channelId kanavan id
     * @throws Exception
     */
    @Transactional
    public void removeConversation(String channelId) throws Exception {
        List<Message> messages = messageRepo.findByChannelId(channelId);
        messageRepo.deleteInBatch(messages);
    }
}

