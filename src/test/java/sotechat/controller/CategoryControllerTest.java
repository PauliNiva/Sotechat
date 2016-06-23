package sotechat.controller;

import org.junit.Before;
import org.junit.Test;
import sotechat.data.*;
import sotechat.util.MockHttpServletRequest;
import java.security.Principal;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    CategoryController categoryController;

    SessionRepo sessionRepo;

    @Before
    public void setUp() {
        sessionRepo = mock(SessionRepo.class);
        categoryController = new CategoryController(sessionRepo);
    }

    @Test
    public void test() {
        when(sessionRepo.updateSession(any(), any())).thenReturn(new Session());
        String category = "hammashoito";
        MockHttpServletRequest req = new MockHttpServletRequest("sessioId09");
        Principal pro = null;
        String response = categoryController
                        .rememberCategoryAndForward(category, req, pro);
        verify(sessionRepo, times(1)).updateSession(any(), any());
        assertEquals("redirect:/", response);
    }

}
