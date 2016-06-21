package sotechat.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sotechat.controller.StateControllerQueueTest;
import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import static org.mockito.Matchers.any;

public class QueueServiceTest {

    QueueService queueService;

    /** Mapper. */
    @Autowired
    private Mapper mapper;

    /** Session Repo. */
    @Autowired
    private SessionRepo sessionRepo;

    /** Database Service. */
    @Autowired
    private DatabaseService databaseService;

    /** Chat Logger. */
    @Autowired
    private ChatLogger chatLogger;

    @Before
    public void setUp() {
        Mockito.when(sessionRepo.getSessionFromSessionId(any(String.class))).thenReturn(new Session());
        queueService = new QueueService();

    }



    //@Test
    public void addToQueueTest() {
        Session session = sessionRepo.getSessionFromUserId("007");
        sessionRepo.updateSessionAttributes(session, null);
        queueService.joinQueue(session, "Mikko", "Heippa!");
    }


}
