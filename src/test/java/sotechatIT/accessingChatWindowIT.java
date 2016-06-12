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
 * As a user I want to access a chat window
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class accessingChatWindowIT {

    private WebDriver driver;
    private WebDriver proDriver;
    private WebDriverWait wait;
    private WebDriverWait proWait;

    @Before
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        proDriver = new ChromeDriver();
        wait = new WebDriverWait(driver, 7);
        proWait = new WebDriverWait(proDriver, 7);
        driver.get(CUSTOMERADDRES);
        proDriver.get(PROADDRES);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        proDriver.quit();
    }

    /**
     * User sees the queuing view
     * User can see a chat window when she/he is picked from a pool of customers by a healthcare professiona
     */
    @Test
    public void UserCanSeeAChatWindow() {
        // User has entered the chat page and submitted a starting message
        waitAndFillInformation(wait);
        // User sees the queuing view
        assertTrue(waitQueueWindowsAppear(wait).isDisplayed());
        // A professional has logged in
        proLogin(proWait);

        // A professional chooses the started conversation from a pool
        waitAndPickFromQueue(proWait);

        // A chat window is opened for the user
        assertTrue(waitChatWindowsAppear(wait).isDisplayed());
    }


}

