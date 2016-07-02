package integrationTests.userAcceptanceTests;


import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import static integrationTests.util.sotechatITCommands.*;
import static integrationTests.util.sotechatITCommands.endConversationPro;
import static integrationTests.util.sotechatITCommands.waitChatWindowsAppear;
import static org.junit.Assert.assertTrue;

@RunWith(WebDriverRunner.class)
@Chrome
public class viewingChatHistoriesIT {

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
    public void proSeesChatHistory() {
        proLogin(proWait);
        handler.getDriver("user").navigate().refresh();
        // User has accessed chat pag
        // Username and a starting message is submitted
        waitAndFillInformation(userWait);
        // A queueing view is showed to the user
        // User is added to the queue of customers professionals side

        waitAndPickFromQueue(proWait);
        waitChatWindowsAppear(userWait);

        waitElementClickable(proWait, By.name("history")).click();
        assertTrue(waitForTextToAppear(proWait, "Menneet keskustelut"));
        assertTrue(waitForTextToAppear(proWait, "Testi"));
        waitElementClickable(proWait, By.name("backToPanel")).click();
        sendMessageChatWindow(proWait, "He hei!");
        waitForTextToAppear(userWait, "He hei!");
        endConversationPro(proWait);
    }
}
