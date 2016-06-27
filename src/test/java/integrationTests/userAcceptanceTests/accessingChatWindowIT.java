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
 * As a user I want to access a chat window
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class accessingChatWindowIT {

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
     * User sees the queuing view
     * User can see a chat window when she/he is picked from a pool of customers by a healthcare professiona
     */
    @Test
    public void UserCanSeeAChatWindow() {
        // A professional has logged in
        proLogin(proWait);
        handler.getDriver("user").navigate().refresh();
        // User has entered the chat page and submitted a starting message
        waitAndFillInformation(userWait);
        // User sees the queuing view
        assertTrue(waitQueueWindowsAppear(userWait).isDisplayed());


        // A professional chooses the started conversation from a pool
        waitAndPickFromQueue(proWait);

        // A chat window is opened for the user
        assertTrue(waitChatWindowsAppear(userWait).isDisplayed());
        endConversationPro(proWait);
    }


}

