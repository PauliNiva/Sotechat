package sotechat.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sotechat.service.AdminService;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@Transactional
public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminService adminService;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {

        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .build();
    }

    @Test
    public void getUsersTest() throws Exception {
        when(adminService.listAllPersonsAsJsonList()).thenReturn("{}");
        mockMvc.perform(MockMvcRequestBuilders.get("/getusers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));

    }

    @Test
    public void delUserValidTest() throws Exception {
        when(adminService.deleteUser("testi")).thenReturn("");
        mockMvc.perform(MockMvcRequestBuilders.delete("/delete/testi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.status", is("OK")));

    }

    @Test
    public void delUserInValidTest() throws Exception {
        when(adminService.deleteUser("testi")).thenReturn("error");
        mockMvc.perform(MockMvcRequestBuilders.delete("/delete/testi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("error")));

    }



    @Test
    public void adduserValidTest() throws Exception {
        when(adminService.addUser("testi")).thenReturn("");
        mockMvc.perform(MockMvcRequestBuilders.post("/newuser")
                .content("testi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    public void adduserInValidTest() throws Exception {
        when(adminService.addUser("testi")).thenReturn("error");
        mockMvc.perform(MockMvcRequestBuilders.post("/newuser")
                .content("testi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("error")));
    }

    @Test
    public void resetPswValidTest() throws Exception {
        when(adminService.changePassword("testi", "testi")).thenReturn("");
        mockMvc.perform(MockMvcRequestBuilders.post("/resetpassword/testi")
                .content("testi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    public void resetPswInValidTest() throws Exception {
        when(adminService.changePassword("testi", "testi")).thenReturn("error");
        mockMvc.perform(MockMvcRequestBuilders.post("/resetpassword/testi")
                .content("testi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("error")));
    }

    @Test
    public void resetDatabaseValidTest() throws Exception {
        when(adminService.clearHistory()).thenReturn("");
        mockMvc.perform(MockMvcRequestBuilders.delete("/tuhoaHistoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    public void resetDatabaseInValidTest() throws Exception {
        when(adminService.clearHistory()).thenReturn("error");
        mockMvc.perform(MockMvcRequestBuilders.delete("/tuhoaHistoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("error")));
    }



}
