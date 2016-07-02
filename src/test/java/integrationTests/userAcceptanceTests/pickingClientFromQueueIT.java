package integrationTests.userAcceptanceTests;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.*;


import static com.github.webdriverextensions.Bot.waitFor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static integrationTests.util.sotechatITCommands.*;

/**
 * As a professional, I want to pick a client from the visible queue
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class pickingClientFromQueueIT {

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
     * Professional can pick the first client from a queue of chats to start a conversation
     */
    @Test
    public void ProPickFirstFromQueue() {
        // Professional has logged in
        proLogin(proWait);
        handler.getDriver("user").navigate().refresh();
        // User has accessed queue
        waitAndFillInformation(userWait);
        waitQueueWindowsAppear(userWait);



        // Professional click´s next in line button
        waitAndPickFromQueue(proWait);

        // a chat window is opened that has a connection to the customer
        waitFor(2, SECONDS);
        assertTrue(waitChatWindowsAppear(proWait).isDisplayed());
        waitChatWindowsAppear(userWait);
        endConversationPro(proWait);
    }

    /**
     * TODO: Professional can pick a customer of her or his choosing
     *  from the chat queue to start a conversation
     * WHEN its implemented
     */

}

