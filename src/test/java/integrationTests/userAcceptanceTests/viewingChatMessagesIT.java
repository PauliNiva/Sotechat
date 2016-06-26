package integrationTests.userAcceptanceTests;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.*;


import static org.junit.Assert.*;
import static integrationTests.util.sotechatITCommands.*;

/**
 * As a user I want to view the messages I have sent in the chat window
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class viewingChatMessagesIT {

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
     * User can see a message that has been sent to the server
     * User can see a message that has been sent by other
     */
    @Test
    public void UserCanSeeAMessages() {
        // User to queue
        waitAndFillInformation(userWait);
        // User from queue
        proLogin(proWait);
        waitAndPickFromQueue(proWait);

        // Can send it By Pressing Submit
        assertEquals("", waitChatWindowsAppear(userWait).getText());
        sendMessageChatWindow(userWait, "yy kaa koo");
        assertTrue(waitForTextToAppear(userWait, "yy kaa koo"));

        // Pro sees it
        assertTrue(waitForTextToAppear(proWait, "yy kaa koo"));

        // Pro sends a message and sees it
        sendMessageChatWindow(proWait, "kaa koo yy"); // changed to pro
        assertTrue(waitForTextToAppear(proWait, "kaa koo yy"));

        // User sees it
        assertTrue(waitForTextToAppear(userWait, "kaa koo yy"));
        endConversationPro(proWait);
    }




}

