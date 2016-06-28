package integrationTests.userAcceptanceTests;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.github.webdriverextensions.Bot.waitFor;
import static integrationTests.util.sotechatITCommands.*;
import static integrationTests.util.sotechatITCommands.endConversationPro;
import static integrationTests.util.sotechatITCommands.waitChatWindowsAppear;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

@RunWith(WebDriverRunner.class)
@Chrome
public class proEndConversationIT {

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
         * As a User I want to end conversation
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
            waitChatWindowsAppear(proWait).isDisplayed();
            waitChatWindowsAppear(userWait);
            endConversationPro(proWait);
            Assert.assertEquals(0, tabsCountToBe(proWait, 0));
        }

    }

