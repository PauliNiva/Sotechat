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
 * Created by varkoi on 8.6.2016.
 */
@Service
public class ConversationService {

    @Autowired
    private ConversationRepo conversationRepo;

    @Autowired
    private PersonRepo personRepo;

    public boolean addPerson(String personId, String channelId)
            throws Exception {
                Conversation conv = conversationRepo.findOne(channelId);
                Person person = personRepo.findOne(personId);
                addConnection(person, conv);
    }

    public void addCategory(String category, String channelId) throws Exception {
            Conversation conv = conversationRepo.findOne(channelId);
            conv.setCategory(category);
    }

    public boolean addMessage(Message message, String ChannelId)
            throws Exception {
        if(conversationRepo.exists(ChannelId)) {
            Conversation conv = conversationRepo.findOne(ChannelId);
            return addMessage(message, conv);
        }else {
            return addConversation(message, ChannelId);
        }
    }

    private boolean addConversation(Message message, String channelId) {
        try {
            Conversation conv = new Conversation();
            conv.setDate(new Date());
            conv.setChannelId(channelId);
//            conv.setCategory(category); //TODO
            return addMessage(message, conv);
        }catch(Exception e){
            return false;
        }
    }

    private boolean addMessage(Message message, Conversation conv){
        try{
            conv.addMessageToConversation(message);
            conversationRepo.save(conv);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private void addConnection(Person person, Conversation conversation) throws Exception {
        conversation.addPersonToConversation(person);
        person.addConversationToPerson(conversation);
        conversationRepo.save(conversation);
        personRepo.save(person);
        }
    }

    public void delete(String channelId) throws Exception {
            conversationRepo.delete(channelId);
    }
}
