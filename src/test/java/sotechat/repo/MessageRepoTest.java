package sotechat.repo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sotechat.Application;
import sotechat.domain.Message;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
@ActiveProfiles("development")
public class MessageRepoTest {

    Message message;

    @Autowired
    MessageRepo messageRepo;

    @Before
    public void setUp() {
        this.message = new Message();
        this.message.setContent("Sisältö");
        this.message.setSender("Pauli");
    }

    @Test
    public void tstii() {

    }
}
