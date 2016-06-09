package sotechatIT;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;


import java.util.ArrayList;

import static org.junit.Assert.*;
import static sotechatIT.sotechatITCommands.*;

/**
 * As a user I want to send a message
 */
public class zproHandlesManyUsersIT {

    private WebDriver proDriver;
    private WebDriverWait proWait;

    @Before
    public void setUp() throws Exception {
        proDriver = new FirefoxDriver();
        proWait = new WebDriverWait(proDriver, 7);
        proDriver.get(PROADDRES);
    }


    @After
    public void tearDown() throws Exception {
        proDriver.close();
    }

    /**
     * What if we have many customer and very very effective Pro
     * Do not try this at home!
     */
    @Test
    public void KillerTest() {
        ArrayList<WebDriver> hyrr = new ArrayList<>();
        int max = 10;
        for (int i = 0; i < max; i++) {
            hyrr.add(new FirefoxDriver());
        }

        for (WebDriver driver : hyrr) {
            driver.get(CUSTOMERADDRES);
            WebDriverWait wait = new WebDriverWait(driver, 4);
            waitAndFillInformation(wait);
        }
        proLogin(proWait);
        for (int i = 0; i < max; i++) {
            waitAndPickFromQueue(proWait);
            sendMessageChatWindow(proWait, "Buhahaha");
        }

        for (WebDriver driver : hyrr) {
            WebDriverWait wait = new WebDriverWait(driver, 4);
            for (int i = 0; i < max; i++) {
                sendMessageChatWindow(wait, "Oletko okei");
            }
        }


        for (WebDriver driver : hyrr) {
            driver.close();
        }

    }


}
