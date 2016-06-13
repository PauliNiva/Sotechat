package sotechat.repo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sotechat.Application;
import sotechat.domain.Person;
import sotechat.domainService.PersonService;


import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
@ActiveProfiles("development")
public class PersonRepoTest {

    Person person;

    @Autowired
    PersonRepo personRepo;

    @Autowired
    PersonService personService;

    @Before
    public void setUp() {
        this.person = new Person();
        this.person.setUserId("jokustringivaansinne");
    }

    @Test
    @Transactional
    public void personIsAddedToRepo() throws Exception {
        this.person.setUsername("Pauli");
        personService.addPerson(this.person, "0000");
        Assert.assertEquals(2, personRepo.count());
    }
}