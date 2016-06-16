package integrationTests.userAcceptanceTests;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;


import static org.junit.Assert.*;
import static integrationTests.util.sotechatITCommands.*;

/**
 * As a professional, I want to see control panel
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class seeingControlPanelIT {

    private DriverHandler handler;
    private WebDriverWait proWait;

    @Before
    public void setUp() throws Exception {
        handler = new DriverHandler("pro");
        handler.HttpGet("pro", PROADDRESS);
        proWait = handler.getWaitDriver("pro");
    }

    @After
    public void tearDown() throws Exception {
        handler.closeAll();
    }

    /**
     * A professional can view a control panel for chat when logged in
     */
    @Test
    public void proCanSeeControlPanelWhenLoggedIn() {
        // Professional logs in with proper username and password
        proLogin(proWait);

        //A control panel can be viewed
       assertTrue(waitElementPresent(proWait, By.className("username-title")).isDisplayed());
    }

    /**
     * A professional can't see a control panel for chat when not logged in
     */
    @Test
    public void proCanNotSeeControlPanelWhenNotLoggedIn() {
        // Professional logs in with proper username and password
        waitElementPresent(proWait,
                By.cssSelector("input[type='submit'][value='Sign In']"))
                .submit();

        //Control panel is not visible to the user
        assertTrue(waitForTextToAppear(proWait,"tunnus tai salasana."));
    }



}

