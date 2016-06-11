package sotechatIT;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;


import static org.junit.Assert.*;
import static sotechatIT.sotechatITCommands.*;

/**
 * As a user I want to send a message
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class sendingMessageIT {

    private WebDriver driver;
    private WebDriver proDriver;
    private WebDriverWait wait;
    private WebDriverWait proWait;

    @Before
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        proDriver = new ChromeDriver();
        wait = new WebDriverWait(driver, 7);
        proWait = new WebDriverWait(proDriver, 7);
        driver.get(CUSTOMERADDRES);
        proDriver.get(PROADDRES);
    }


    @After
    public void tearDown() throws Exception {
        driver.close();
        proDriver.close();
    }

    /**
     * User can write a message on a text input
     * User can send the message he or she has written to the server by pressing submit
     * User can send the message he or she has written to the server by pressing enter
     */
    @Test
    public void UserCanWriteAndSendAMessage() {
        // User to queue
        waitAndFillInformation(wait);
        // User from queue
        proLogin(proWait);
        waitAndPickFromQueue(proWait);

        // User can write message
        waitChatWindowsAppear(wait).sendKeys("yy kaa koo");

        // Can send it By Pressing Submit
        waitElementPresent(wait, By.name("send")).submit();
        assertEquals("", waitChatWindowsAppear(wait).getText());
        assertTrue(waitForTextToAppear(wait, "yy kaa koo"));

        // Can send it By Pressing Enter
        waitChatWindowsAppear(wait).sendKeys("kaa koo yy");
        waitChatWindowsAppear(wait).sendKeys(Keys.ENTER);

        assertEquals("", waitChatWindowsAppear(wait).getText());
        assertTrue(waitForTextToAppear(wait, "kaa koo yy"));
    }



}

