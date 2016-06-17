package integrationTests.userAcceptanceTests;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.*;


import static org.junit.Assert.*;
import static integrationTests.util.sotechatITCommands.*;

/**
 * As a user I want to send a message
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class sendingMessageIT {

    private WebDriver driver;
    private DriverHandler handler;
    private WebDriverWait userWait;
    private WebDriverWait proWait;

    @Before
    public void setUp() throws Exception {
        handler = new DriverHandler("user", "pro");
        handler.HttpGet("user", CUSTOMERADDRESS);
        handler.HttpGet("pro", PROADDRESS);
        userWait = handler.getWaitDriver("user");
        proWait = handler.getWaitDriver("pro");
    }


    @After
    public void tearDown() throws Exception {
        handler.closeAll();
    }

    /**
     * User can write a message on a text input
     * User can send the message he or she has written to the server by pressing submit
     * User can send the message he or she has written to the server by pressing enter
     */
    @Test
    public void UserCanWriteAndSendAMessage() {
        // User to queue
        waitAndFillInformation(userWait);
        // User from queue
        proLogin(proWait);
        waitAndPickFromQueue(proWait);

        // User can write message
        waitChatWindowsAppear(userWait).sendKeys("yy kaa koo");

        // Can send it By Pressing Submit
        waitElementPresent(userWait, By.name("send")).submit();
        assertEquals("", waitChatWindowsAppear(userWait).getText());
        assertTrue(waitForTextToAppear(userWait, "yy kaa koo"));

        // Can send it By Pressing Enter
        waitChatWindowsAppear(userWait).sendKeys("kaa koo yy");
        waitChatWindowsAppear(userWait).sendKeys(Keys.ENTER);

        assertEquals("", waitChatWindowsAppear(userWait).getText());
        assertTrue(waitForTextToAppear(userWait, "kaa koo yy"));
    }



}

