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
public class receivingMessagesIT {

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
     * As a user I want to see the messages other people have sent to discussion
     */
    @Test
    public void UserSeesOtherPeopleMessages() {
        // User has accessed queue
        waitAndFillInformation(wait);
        waitQueueWindowsAppear(wait);

        // Professional has logged in & next in line button
        proLogin(proWait);
        waitAndPickFromQueue(proWait);
        // The other person sends a message
        sendMessageChatWindow(proWait,"Can you see this message?");

        // User can view it in the chat window
        assertTrue(waitForTextToAppear(wait,"Can you see this message?"));
    }



}

