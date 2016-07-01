package integrationTests.userAcceptanceTests;


import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import org.openqa.selenium.support.ui.*;

import static org.junit.Assert.*;
import static integrationTests.util.sotechatITCommands.*;

/**
 * As a user, I want to join the queue to wait my turn
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class joiningQueueIT {

    private WebDriverWait userWait;
    private DriverHandler handler;
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
     * User joins a common queue when accessing chat
     */
    @Test
    public void UserJoinsACommonQueue() {
        proLogin(proWait);
        handler.getDriver("user").navigate().refresh();
        // User has accessed chat pag
        // Username and a starting message is submitted
        waitAndFillInformation(userWait);

        // A queueing view is showed to the user
        assertTrue(waitForTextToAppear(userWait, "sinua palvellaan mahdollisimman pian"));

        // User is added to the queue of customers professionals side

        assertTrue(waitElementPresent(proWait,By.id("queuerBlock")).isDisplayed());
        waitAndPickFromQueue(proWait);
        waitChatWindowsAppear(userWait);
        endConversationPro(proWait);
    }



}

