package integrationTests.userAcceptanceTests;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.validation.constraints.AssertTrue;

import static com.github.webdriverextensions.Bot.waitFor;
import static integrationTests.util.sotechatITCommands.*;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(WebDriverRunner.class)
@Chrome
public class userEndConversationIT {

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
     * Professional can end conversation when he/she wants
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
        waitChatWindowsAppear(proWait).isDisplayed();
        waitChatWindowsAppear(userWait);
        waitElementClickable(userWait, By.name("endConversation")).click();
        waitElementClickable(userWait, By.name("sure")).click();
        Assert.assertTrue(waitForTextToAppear(userWait, "Keskustelu on "));
        Assert.assertTrue(waitForTextToAppear(proWait, "Vastapuoli on "));
        waitElementClickable(proWait, By.name("closeConversation")).click();
        Assert.assertEquals(0, tabsCountToBe(proWait, 0));

    }
}
