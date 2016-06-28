package sotechat.service;


import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;

public class AdminServiceTest {

    Person person;
    String jsonPerson;

    @Autowired
    AdminService adminService;

    @Autowired
    PersonRepo personRepo;

    public void setUp() {
        person = personRepo.findOne("admin");
        Gson gson = new Gson();
        jsonPerson = gson.toJson(person);
    }

    //public void testi() {
    //    adminService.addUser(jsonPerson) {
            
    //    }
    //}

}
