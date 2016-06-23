package sotechat.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sotechat.Launcher;
import sotechat.data.*;
import sotechat.service.ValidatorService;
import sotechat.util.MockHttpServletRequest;
import sotechat.util.MockMockHttpSession;
import sotechat.util.MockPrincipal;
import sotechat.util.MockSession;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.HashMap;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@WebAppConfiguration
@Transactional
public class HistoryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SessionRepo sessionRepo;

    MockMvc mockMvc;
    Mapper mapper;

    @Before
    public void setUp() throws Exception {
        this.mapper = (Mapper) webApplicationContext.getBean("mapper");
        this.mapper.mapProUsernameToUserId("Hoitaja", "666");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getConversationValidTest() throws Exception {
        HttpServletRequest mockRequest = new MockHttpServletRequest("666");
        Principal mockPrincipal = new MockPrincipal("Hoitaja");
        Session proSession = sessionRepo.updateSession(mockRequest, mockPrincipal);
        Channel channel = this.mapper.createChannel();
        channel.addSubscriber(proSession);

        mockMvc.perform(MockMvcRequestBuilders.get("/listMyConversations/")
                .session(new MockMockHttpSession("666"))
                .principal(new MockPrincipal("Hoitaja")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));

    }

    @Test
    public void getConversationInvalidTest() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/listMyConversations/")
                .session(new MockMockHttpSession("666")))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals("", content);
    }

    @Test
    public void getMessagesValidTest() throws Exception {
        HttpServletRequest mockRequest = new MockHttpServletRequest("666");
        Principal mockPrincipal = new MockPrincipal("Hoitaja");
        Session proSession = sessionRepo.updateSession(mockRequest, mockPrincipal);
        Channel channel = this.mapper.createChannel();
        channel.addSubscriber(proSession);

        mockMvc.perform(MockMvcRequestBuilders.get("/getLogs/" + channel.getId())
                .session(new MockMockHttpSession("666"))
                .principal(new MockPrincipal("Hoitaja")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }


    @Test
    public void getMessagesInvalidTest() throws Exception {
        HttpServletRequest mockRequest = new MockHttpServletRequest("666");
        Principal mockPrincipal = new MockPrincipal("Hoitaja");
        Session proSession = sessionRepo.updateSession(mockRequest, mockPrincipal);
        Channel channel = this.mapper.createChannel();
        channel.addSubscriber(proSession);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/getLogs/" + channel.getId())
                .session(new MockMockHttpSession("666")))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals("", content);
    }



}
