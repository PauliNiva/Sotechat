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

    @Autowired
    public MessageService(MessageRepo pMessageRepo) throws Exception{
        this.messageRepo = pMessageRepo;
    }

    @Transactional
    public void addMessage(Message message) throws Exception {
        messageRepo.save(message);
    }

    @Transactional
    public Message getMessage(Long messageId) throws Exception {
        return messageRepo.findOne(messageId);
    }

    @Transactional
    public void removeMessage(Long messageId) throws Exception {
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

}

