package sotechatIT;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;


import static org.junit.Assert.*;
import static sotechatIT.sotechatITCommands.*;

/**
 * As a professional, I want to see control panel
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class seeingControlPanelIT {

    private WebDriver proDriver;
    private WebDriverWait proWait;

    @Before
    public void setUp() throws Exception {
        proDriver = new ChromeDriver();
        proWait = new WebDriverWait(proDriver, 7);
        proDriver.get(PROADDRES);
    }


    @After
    public void tearDown() throws Exception {
        proDriver.quit();
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
                By.name("login"))
                .submit();

        //Control panel is not visible to the user
        assertTrue(waitForTextToAppear(proWait,"There was a problem"));
    }



}

