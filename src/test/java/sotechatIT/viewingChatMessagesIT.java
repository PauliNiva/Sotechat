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
 * As a user I want to view the messages I have sent in the chat window
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class viewingChatMessagesIT {

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
        driver.close();
        proDriver.close();
    }

    /**
     * User can see a message that has been sent to the server
     * User can see a message that has been sent by other
     */
    @Test
    public void UserCanSeeAMessages() {
        // User to queue
        waitAndFillInformation(wait);
        // User from queue
        proLogin(proWait);
        waitAndPickFromQueue(proWait);

        // Can send it By Pressing Submit
        sendMessageChatWindow(wait, "yy kaa koo");
        assertEquals("", waitChatWindowsAppear(wait).getText());
        assertTrue(waitForTextToAppear(wait, "yy kaa koo"));

        // Pro sees it
        assertTrue(waitForTextToAppear(proWait, "yy kaa koo"));

        // Pro sends a message and sees it
        sendMessageChatWindow(wait, "kaa koo yy");
        assertTrue(waitForTextToAppear(proWait, "kaa koo yy"));

        // User sees it
        assertTrue(waitForTextToAppear(wait, "kaa koo yy"));

    }




}

