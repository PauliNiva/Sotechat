package sotechat;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class LauncherTest {

    Launcher launcher;

    @Before
    public void setUp() {
        launcher = new Launcher();
    }

    @Test
    public void LauncherInitializesSpringApplication() {
        ApplicationContext ctx = launcher.launch(new String[]{"test1",
                "test2"});
        Assert.assertEquals("", ctx.getApplicationName().toString());
    }
}
