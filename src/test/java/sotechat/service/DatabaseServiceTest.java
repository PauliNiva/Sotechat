package sotechat.service;

import org.apache.poi.ss.formula.functions.T;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sotechat.Application;
import sotechat.domain.Conversation;
import sotechat.domain.Message;
import sotechat.domain.Person;
import sotechat.domainService.ConversationService;
import sotechat.domainService.MessageService;
import sotechat.domainService.PersonService;
import sotechat.repo.ConversationRepo;
import sotechat.repo.MessageRepo;
import sotechat.repo.PersonRepo;

import javax.transaction.Transactional;

/**
 * Created by varkoi on 14.6.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
@ActiveProfiles("development")
public class DatabaseServiceTest {

    Person person;

    @Autowired
    PersonRepo pr;

    @Autowired
    ConversationRepo cr;

    @Autowired
    MessageRepo mr;

    @Autowired
    PersonService pservice;

    @Autowired
    ConversationService cservice;

    @Autowired
    MessageService mservice;

    @Autowired
    DatabaseService dbservice;

    @Before
    public void setUp() throws Exception {
        person = new Person("xxd");
        pservice.addPerson(person, "salasana");
    }

    @Test
    public void createConversationTest() throws Exception {
        dbservice.createConversation("Anon", "Moi!", "888a", "hammashoito");
        Assert.assertEquals(cr.findAll().get(0))
    }
}
