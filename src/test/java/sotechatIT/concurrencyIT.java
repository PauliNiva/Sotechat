package sotechatIT;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sotechatIT.sotechatITCommands.*;
import static sotechatIT.sotechatITCommands.sendMessageChatWindow;
import static sotechatIT.sotechatITCommands.waitForTextToAppear;

/**
 * As a user I want to view the messages I have sent in the chat window
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class concurrencyIT {

    private DriverHandler handler;
    private WebDriverWait userWait;
    private WebDriverWait proWait;

    @Before
    public void setUp() throws Exception {

    }


    @After
    public void tearDown() throws Exception {
        handler.closeAll();
    }

    //@Test
    public void UserCanSeeAMessages() throws Exception {
        for (int i=1; i<1; i++) {
            System.out.println("Starting i = " + i);
            repeat();
        }

    }

    public void repeat() throws Exception {
        handler = new DriverHandler("user", "pro");
        handler.HttpGet("user", CUSTOMERADDRESS);
        handler.HttpGet("pro", PROADDRESS);
        userWait = handler.getWaitDriver("user");
        proWait = handler.getWaitDriver("pro");

        // User to queue
        waitAndFillInformation(userWait);
        // User from queue
        proLogin(proWait);
        waitAndPickFromQueue(proWait);

        assertEquals("", waitChatWindowsAppear(userWait).getText());
        assertEquals("", waitChatWindowsAppear(proWait).getText());
        assertTrue(waitForTextToAppear(userWait, "Moikkamoi!"));
        assertTrue(waitForTextToAppear(proWait, "Moikkamoi!"));

        sendMessageChatWindow(userWait, "yy kaa koo");
        assertTrue(waitForTextToAppear(userWait, "yy kaa koo"));
        assertTrue(waitForTextToAppear(proWait, "yy kaa koo"));

        sendMessageChatWindow(proWait, "kaa koo yy");
        assertTrue(waitForTextToAppear(proWait, "kaa koo yy"));
        assertTrue(waitForTextToAppear(userWait, "kaa koo yy"));

        handler.closeAll();
    }





}

