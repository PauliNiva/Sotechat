package sotechat.domainService;

import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Message;
import sotechat.domain.Conversation;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;

@Service
public class MessageService  {

    private MessageRepo messageRepo;

    private ConversationRepo conversationRepo;

    @Autowired
    public MessageService(MessageRepo pMessageRepo,
                          ConversationRepo pConversationRepo) {
        this.messageRepo = pMessageRepo;
        this.conversationRepo = pConversationRepo;
    }

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

    public void setMessageRepo(MessageRepo pMessageRepo) {
        this.messageRepo = pMessageRepo;
    }

    public MessageRepo getMessageRepo() {
        return this.messageRepo;
    }

    public void setConversationRepo(ConversationRepo pConversationRepo) {
        this.conversationRepo = pConversationRepo;
    }

    public ConversationRepo getConversationRepo() {
        return this.conversationRepo;
    }
}

