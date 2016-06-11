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
                          ConversationRepo pConversationRepo) 
                          throws Exception{
        this.messageRepo = pMessageRepo;
        this.conversationRepo = pConversationRepo;
    }

    @Transactional
    public void addMessage(Message message) throws Exception {
        messageRepo.save(message);
    }

    @Transactional
    public void removeMessage(Long messageId) throws Exception {
        Message message = messageRepo.findOne(messageId);
        String conversationId = message.getConversation().getChannelId();
        conversationRepo.findOne(conversationId).getMessagesOfConversation()
                .remove(message);
        messageRepo.delete(messageId);
    }

    public List<Message> messagesOfConversation(String channelId) throws Exception {
        return messageRepo.findByConversation(channelId);
    }

    @Transactional
    public void removeConversation(String channelId) throws Exception {
        List<Message> messages= messageRepo.findByConversation(channelId);
        messageRepo.deleteInBatch(messages);
    }

    public void setMessageRepo(MessageRepo pMessageRepo) throws Exception {

        this.messageRepo = pMessageRepo;
    }

    public MessageRepo getMessageRepo() throws Exception {
        return this.messageRepo;
    }

    public void setConversationRepo(final ConversationRepo pConversationRepo) throws Exception {
        this.conversationRepo = pConversationRepo;
    }

    public ConversationRepo getConversationRepo() throws Exception {

        return this.conversationRepo;
    }
}

