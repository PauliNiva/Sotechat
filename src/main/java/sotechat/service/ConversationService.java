package sotechat.service;

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

    public boolean addConversation(Long personId, String category){
        try {
            Conversation conv = new Conversation();
            conv.setDate(new Date());
            conv.setCategory(category);
            Person person = personRepo.findOne(personId);
            addConnection(person, conv);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public boolean addPerson(Long personId, Long conversationId){
        try {
            Conversation conv = conversationRepo.findOne(conversationId);
            Person person = personRepo.findOne(personId);
            addConnection(person, conv);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    public boolean addMessage(Message message, Long conversationId){
        try{
            Conversation conv = conversationRepo.findOne(conversationId);
            conv.addMessageToConversation(message);
            conversationRepo.save(conv);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private void addConnection(Person person, Conversation conversation) throws Exception {
        if(personRepo.exists(person.getId())) {
            conversation.addPersonToConversation(person);
            person.addConversationToPerson(conversation);
            conversationRepo.save(conversation);
            personRepo.save(person);
        }else{
            throw new Exception();
        }
    }

    public boolean delete(Long conversationId){
            conversationRepo.delete(conversationId);
    }
}
