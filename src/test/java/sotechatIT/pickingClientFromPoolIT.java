package sotechatIT;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;


import static org.junit.Assert.*;
import static sotechatIT.sotechatITCommands.*;

/**
 * As a professional, I want to pick a client from the visible queue
 */
public class pickingClientFromPoolIT {

    private WebDriver driver;
    private WebDriver proDriver;
    private WebDriverWait wait;
    private WebDriverWait proWait;

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        proDriver = new FirefoxDriver();
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
     * Professional can pick the first client from a pool of chats to start a conversation
     */
    @Test
    public void ProPickFirstFromQueue() {
        // User has accessed queue
        waitAndFillInformation(wait);
        waitQueueWindowsAppear(wait);

        // Professional has logged in
        proLogin(proWait);

        // Professional click´s next in line button
        waitAndPickFromQueue(proWait);

        // a chat window is opened that has a connection to the customer
        assertTrue(waitChatWindowsAppear(proWait).isDisplayed());
        waitChatWindowsAppear(wait);
    }

    /**
     * TODO: Professional can pick a customer of her or his choosing
     *  from the chat pool to start a conversation
     * WHEN its implemented
     */

}

