package sotechat.domainService;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Message;
import sotechat.domain.Conversation;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;

@Service
public class MessageService  {

    private MessageRepo messageRepo;

    private ConversationRepo conversationRepo;

    private PersonRepo personRepo;

    @Autowired
    public MessageService(MessageRepo pMessageRepo,
                          ConversationRepo pConversationRepo) {
        this.messageRepo = pMessageRepo;
        this.conversationRepo = pConversationRepo;
    }

    @Transactional
    public void addMessage(Message message) {
        messageRepo.save(message);
    }

    @Transactional
    public void removeMessage(Long messageId) {
        messageRepo.delete(messageId);
    }

    public List<Message> messagesOfConversation(String channelId){
        return messageRepo.findByConversation(channelId);
    }

    public void removeConversation(String channelId){
        List<Message> messages= messageRepo.findByConversation(channelId);
        messageRepo.deleteInBatch(messages);
    }

    @Transactional
    public void deleteMessagesOfConversation(String channelId){
        List<Message> messages = messageRepo.findByConversation(channelId);
        messageRepo.deleteInBatch(messages);
    }

    public void setMessageRepo(MessageRepo pMessageRepo) {

        this.messageRepo = pMessageRepo;
    }

    public MessageRepo getMessageRepo() {
        return this.messageRepo;
    }

    public void setConversationRepo(final ConversationRepo pConversationRepo) {
        this.conversationRepo = pConversationRepo;
    }

    public ConversationRepo getConversationRepo() {

        return this.conversationRepo;
    }
}

