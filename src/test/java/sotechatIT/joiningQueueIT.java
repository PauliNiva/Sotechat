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
 * As a user, I want to join the queue to wait my turn
 */
public class joiningQueueIT {

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
     * User joins a common queue when accessing chat
     */
    @Test
    public void UserJoinsACommonQueue() {
        // User has accessed chat pag
        // Username and a starting message is submitted
        waitAndFillInformation(wait);

        // A queueing view is showed to the user
        assertTrue(waitQueueWindowsAppear(wait).isDisplayed());

        // User is added to the pool of customers professionals side
        proLogin(proWait);
        assertTrue(waitElementPresent(proWait,By.id("queuerBlock")).isDisplayed());
        waitAndPickFromQueue(proWait);
        waitChatWindowsAppear(wait);
    }



}

