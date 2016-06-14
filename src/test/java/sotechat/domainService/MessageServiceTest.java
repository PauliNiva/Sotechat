package sotechat.domainService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sotechat.Application;
import sotechat.Launcher;
import sotechat.domain.Message;
import sotechat.repo.MessageRepo;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@Transactional
@ActiveProfiles("development")
public class MessageServiceTest {

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
