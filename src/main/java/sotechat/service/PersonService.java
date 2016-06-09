package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Conversation;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.PersonRepo;

import java.util.List;

/**
 * Created by varkoi on 8.6.2016.
 */
@Service
public class PersonService {

    @Autowired
    private PersonRepo personRepo;

    public boolean addPerson(Person person, String password){
        try {
            person.setPassword(password);
            personRepo.save(person);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public Person findOne(Long personId) throws Exception {
        return personRepo.findOne(personId);
    }

    public List<Person> findAll(){
        return personRepo.findAll();
    }

    public void delete(Long personId) throws Exception {
        personRepo.delete(personId);
    }

    public boolean changePassword(Long personId, String password){
        try {
            Person person = personRepo.findOne(personId);
            person.setPassword(password);
            personRepo.save(person);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public boolean changeScreenName(Long personId, String newName){
        try {
            Person person = personRepo(personId);
            person.setScreenName(newName);
            personRepo.save(person);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
