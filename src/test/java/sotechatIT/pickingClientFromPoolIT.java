package sotechatIT;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.*;


import static org.junit.Assert.*;
import static sotechatIT.sotechatITCommands.*;

/**
 * As a professional, I want to pick a client from the visible queue
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class pickingClientFromPoolIT {

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
     * Professional can pick the first client from a pool of chats to start a conversation
     */
    @Test
    public void ProPickFirstFromQueue() {
        // User has accessed queue
        waitAndFillInformation(userWait);
        waitQueueWindowsAppear(userWait);

        // Professional has logged in
        proLogin(proWait);

        // Professional click´s next in line button
        waitAndPickFromQueue(proWait);

        // a chat window is opened that has a connection to the customer
        assertTrue(waitChatWindowsAppear(proWait).isDisplayed());
        waitChatWindowsAppear(userWait);
    }

    /**
     * TODO: Professional can pick a customer of her or his choosing
     *  from the chat pool to start a conversation
     * WHEN its implemented
     */

}

