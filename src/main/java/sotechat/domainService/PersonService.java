package sotechat.domainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PersonService {

    private final PersonRepo personRepo;

    @Autowired
    public PersonService(PersonRepo pPersonRepo) {
        this.personRepo = pPersonRepo;
    }

    @Transactional
    public void addPerson() {
        Person pPerson = new Person();
        pPerson.setUsername("apina");
        pPerson.setPassword("apina");
        personRepo.save(pPerson);
    }

    public void getPersons() {
        List<Person> persons = personRepo.findAll();
        for (Person p : persons) {
            System.out.println(p.getId());
        }
    }
}
