package sotechatIT;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;


import java.util.ArrayList;
import java.util.List;
import static sotechatIT.sotechatITCommands.*;

/**
 * As a user I want to send a message
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class zproHandlesManyUsersIT {

    private static final int MAX_CUSTOMERS = 10;
    private DriverHandler handler;
    private WebDriverWait proWait;
    private List<WebDriver> customers;

    @Before
    public void setUp() throws Exception {
        handler = new DriverHandler("pro");
        handler.HttpGet("pro", PROADDRESS);

        /** Load max webdrivers representing customers. */
        customers = new ArrayList<>(MAX_CUSTOMERS);
        for (int i = 0; i < MAX_CUSTOMERS; i++) {
            String username = "user" + i;
            customers.add(handler.addDriver(username));
        }
    }


    @After
    public void tearDown() throws Exception {
        handler.closeAll();
    }

    /**
     * What if we have many customer and very very effective Pro
     * Do not try this at home!
     */
    
    public void KillerTest() {

        /** All customers join the queue. */
        for (WebDriver customer : customers) {
            customer.get(CUSTOMERADDRESS);
            WebDriverWait wait = new WebDriverWait(customer, 4);
            waitAndFillInformation(wait);
        }

        /** Pro logs in and for each customer: polls from queue, sends msg. */
        proWait = handler.getWaitDriver("pro");
        proLogin(proWait);
        for (int i = 0; i < MAX_CUSTOMERS; i++) {
            waitAndPickFromQueue(proWait);
            sendMessageChatWindow(proWait, "Buhahaha");
        }

        /** Customers send many messages. */
        for (WebDriver customer : customers) {
            customer.get(CUSTOMERADDRESS);
            WebDriverWait wait = new WebDriverWait(customer, 4);
            for (int i = 0; i < MAX_CUSTOMERS; i++) {
                sendMessageChatWindow(wait, "Oletko okei");
            }
        }

    }


}

