package integrationTests.additionalTests;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static integrationTests.util.sotechatITCommands.*;
import static integrationTests.util.sotechatITCommands.sendMessageChatWindow;
import static integrationTests.util.sotechatITCommands.waitForTextToAppear;

/**
 * As a user I want to view the messages I have sent in the chat window
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class fastGodIT {

    @Test
    public void typicalFlow() throws Exception {
        DriverHandler handler = new DriverHandler("user", "pro");
        handler.HttpGet("user", CUSTOMERADDRESS);
        handler.HttpGet("pro", PROADDRESS);
        WebDriverWait userWait = handler.getWaitDriver("user");
        WebDriverWait proWait = handler.getWaitDriver("pro");

        /** Kayttaja menee jonoon aloitusviestilla "Moikkamoi!" */
        waitAndFillInformation(userWait);

        /** Hoitaja loggaa sisaan ja ottaa kayttajan jonosta. */
        proLogin(proWait);
        waitAndPickFromQueue(proWait);

        /** Aloitusviestin nakyminen molemmille.
         * Nimenomaan taman kanssa on tullut samanaikaisuusvirheita joskus. */
        assertEquals("", waitChatWindowsAppear(userWait).getText());
        assertEquals("", waitChatWindowsAppear(proWait).getText());
        assertTrue(waitForTextToAppear(userWait, "Moikkamoi!"));
        assertTrue(waitForTextToAppear(proWait, "Moikkamoi!"));

        /** Kayttajan lahettama viesti nakyy molemmille. */
        sendMessageChatWindow(userWait, "yy kaa koo");
        assertTrue(waitForTextToAppear(userWait, "yy kaa koo"));
        assertTrue(waitForTextToAppear(proWait, "yy kaa koo"));

        /** Hoitajan lahettama viesti nakyy molemmille. */
        sendMessageChatWindow(proWait, "kaa koo yy");
        assertTrue(waitForTextToAppear(proWait, "kaa koo yy"));
        assertTrue(waitForTextToAppear(userWait, "kaa koo yy"));

        handler.closeAll();
    }





}

