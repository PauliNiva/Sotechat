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
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

@Service
public class MessageService {

    //@Autowired
    private MessageRepo messageRepo;

    //@Autowired
    private ConversationRepo conversationRepo;

    private PersonRepo personRepo;

    @Transactional
    public Message addMessage(Message message) {
        messageRepo.save(message);
    }

    @Transactional
    public void removeMessage(Long messageId) {
        Message message = messageRepo.findOne(messageId);
        String conversationId = message.getConversation().getChannelId();
        conversationRepo.findOne(conversationId).getMessagesOfConversation()
                .remove(message);
        messageRepo.delete(messageId);
    }
}

