package sotechat.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sotechat.data.Mapper;
import sotechat.data.MapperImpl;

import javax.servlet.http.HttpServletRequest;

import java.security.Principal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ChatControllerTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class ChatControllerTest {

    @Autowired
    private WebApplicationContext context;

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
                .standaloneSetup(new ChatController(new MapperImpl())).build();
    }

    /**
     * Get pyyntö polkuun "/join" palauttaa statukseksen OK.
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
     * @throws Exception
     */
    @Test
    public void getToProIsValid() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/pro").content("Tänne tulisi hoitajan näkymä"));
    }
}
