package sotechat.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import sotechat.data.Mapper;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;

import javax.transaction.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    PersonRepo personRepo;

    @Autowired
    DatabaseService databaseService;

    @Autowired
    Mapper mapper;

    private Person person;

    @Transactional
    public boolean addUser(String jsonPerson) throws Exception {
        Gson gson = new Gson();
        Type type = new TypeToken<Person>(){}.getType();
        this.person = gson.fromJson(jsonPerson, type);
        if (person.getLoginName().isEmpty() || person.getPassword().isEmpty() ||
                person.getUserName().isEmpty()) {
            return false;
        } else {
            this.person.setUserId(mapper.generateNewId());
            String passwordToBeSet = person.getPassword();
            person.setPassword(passwordToBeSet);
            person.setRole("ROLE_USER");
        }
        try {
            personRepo.save(this.person);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Transactional
    public String listAllPersonsAsJsonList() {
        List<Person> personList = personRepo.findAll();
        List<Person> deprecatedPersonList = new ArrayList<>();
        personList.forEach(person->deprecatedPersonList
                .add(extractInfo(person)));
        Gson gson = new Gson();
        return gson.toJson(deprecatedPersonList);
    }

    public Person extractInfo(Person person) {
        Person personWithDeprecatedAttributes = new Person();
        personWithDeprecatedAttributes.setLoginName(person.getLoginName());
        personWithDeprecatedAttributes.setUserName(person.getUserName());
        personWithDeprecatedAttributes.setUserId(person.getUserId());
        return personWithDeprecatedAttributes;
    }

    @Transactional
    public boolean deleteUser(String userId) throws Exception {
        try {
            personRepo.delete(userId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Transactional
    public boolean changePassword(String id, String newPassword) throws Exception {
        try {
            this.person = personRepo.findOne(id);
        } catch (Exception e) {
            return false;
        }
        this.person.setPassword(newPassword);
        return true;
    }

    @Transactional
    public void resetDatabase() {
        databaseService.resetDatabase();
    }
}
