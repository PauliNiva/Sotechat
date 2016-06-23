package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    PersonService personService;

    @Autowired
    PersonRepo personRepo;

    Person person;

    public Person makePerson() {
        this.person = new Person();
        return this.person;
    }

    @Transactional
    public void addUser(Person pPerson) {
        personRepo.save(pPerson);
    }

    @Transactional
    public List<Person> listAllPersons() {
        return personRepo.findAll();
    }

    @Transactional
    public void deleteUser(String userId) {
        personRepo.delete(userId);
    }
}
