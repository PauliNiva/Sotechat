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
 * As a professional, I want to pick a client from the visible queue
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class receivingMessagesIT {

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
     * As a user I want to see the messages other people have sent to discussion
     */
    @Test
    public void UserSeesOtherPeopleMessages() {
        // User has accessed queue
        waitAndFillInformation(userWait);
        waitQueueWindowsAppear(userWait);

        // Professional has logged in & next in line button
        proLogin(proWait);
        waitAndPickFromQueue(proWait);
        // The other person sends a message
        sendMessageChatWindow(proWait,"Can you see this message?");

        // User can view it in the chat window
        assertTrue(waitForTextToAppear(userWait,"Can you see this message?"));
        endConversationPro(proWait);
    }



}

