package sotechat.service;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Message;
import sotechat.domain.Conversation;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;

@Service
public class MessageService {

    //@Autowired
    private MessageRepo messageRepo;

    //@Autowired
    private ConversationRepo conversationRepo;

    @Transactional
    public void addMessage(Message pMessage) {
        messageRepo.save(pMessage);
        pMessage.setDate(new Date());
        Conversation conversation = pMessage.getConversation();
        conversation.getMessagesOfConversation().add(pMessage);

        messageRepo.save(pMessage);
    }

    @Transactional
    public void removeMessage(Long messageId) {
        Message message = messageRepo.findOne(messageId);
        Long conversationId = message.getConversation().getId();
        conversationRepo.findOne(conversationId).getMessagesOfConversation()
                .remove(message);
        messageRepo.delete(messageId);
    }
}

