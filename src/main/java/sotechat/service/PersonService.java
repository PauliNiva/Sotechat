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

    public void addPerson(Person person, String password){
        person.setPassword(password);
        personRepo.save(person);
    }

    public void findOne(Long id){
        return personRepo.findOne(id);
    }

    public List<Person> findAll(){
        return personRepo.findAll();
    }

    public void delete(Long id){
        personRepo.delete(id);
    }

}
