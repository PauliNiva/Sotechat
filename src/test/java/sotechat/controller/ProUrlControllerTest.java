package sotechat.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sotechat.Launcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@WebAppConfiguration
public class ProUrlControllerTest {

        @Autowired
        private WebApplicationContext webApplicationContext;

        private MockMvc mvc;

        /** Before.
         * @throws Exception
         */
        @Before
        public void setUp() throws Exception {
            mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        /**
         * @throws Exception
         */
        @Test
        public void testProUrl() throws Exception {
            mvc.perform(MockMvcRequestBuilders
                    .get("/pro")
                    .accept(MediaType.TEXT_HTML))
                    .andExpect(status().isOk())
                    .andExpect(forwardedUrl("/proCP.html"));
        }

        @Test
        public void testLoginUrl() throws Exception {
            mvc.perform(MockMvcRequestBuilders
                    .get("/login")
                    .accept(MediaType.TEXT_HTML))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/pro"));
;
        }
}
