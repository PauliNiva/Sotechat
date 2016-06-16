package sotechat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class ApplicationTest {

    Launcher mockStarter;

    @Before
    public void setUp() {
        mockStarter = mock(Launcher.class);
        Application application = new Application();
    }

    @Test
    public void mainTest() throws Exception {
        Application.setLauncher(mockStarter);
        Application.main(new String[]{"test1", "test2"});
        Mockito.verify(mockStarter, times(1)).launch(new String[]{"test1", "test2"});
    }

}
