package sotechat.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sotechat.data.MapperImpl;
import sotechat.queue.Queue;
import sotechat.queue.QueueImpl;
import sotechat.service.QueueService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ChatControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class ChatControllerTest {

    /**
     * MockMvc.
     */
    private MockMvc mvc;

    /**
     * Before.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders
                .standaloneSetup(new ChatController(new MapperImpl(),
                        new SubscribeEventHandler(),
                        new QueueService(new QueueImpl()))).build();
    }

    /**
     * Get pyyntö polkuun "/join" palauttaa statukseksen OK.
     * TODO: Kunnolla.
     * @throws Exception
     */
    @Test
    public void getToJoinReturnOK() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/join").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Get pyyntö polkuun "/pro" palauttaa merkkijonon.
     * TODO: Kunnolla.
     * @throws Exception
     */
    @Test
    public void getToProIsValid() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/pro").content("Tänne tulisi hoitajan näkymä"));
    }
}

