package integrationTests.userAcceptanceTests;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.*;


import static org.junit.Assert.*;
import static integrationTests.util.sotechatITCommands.*;

/**
 * As a professional, I want to login and logout
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class proLoginLogoutIT {

    private DriverHandler handler;
    private WebDriverWait proWait;

    @Before
    public void setUp() throws Exception {
        handler = new DriverHandler("pro");
        handler.HttpGet("pro", PROADDRESS);
        proWait = handler.getWaitDriver("pro");
    }


    @After
    public void tearDown() throws Exception {
        handler.closeAll();
    }

    /**
     * As a professional I want to login with valid data
     */
    @Test
    public void proLoginValid() {
       // Pro logs in
        proLogin(proWait);
        assertTrue(waitForTextToAppear(proWait, "jonossa"));
    }

    @Test
    public void proLoginEmpty() {
        waitElementPresent(proWait, By.name("username")).sendKeys("hoitaja");
        waitElementPresent(proWait, By.name("password")).sendKeys("");
        waitElementPresent(proWait, By.name("login")).submit();
        assertTrue(waitForTextToAppear(proWait, "on virheellinen"));
        waitElementPresent(proWait, By.name("username")).sendKeys("");
        waitElementPresent(proWait, By.name("password")).sendKeys("salasana");
        waitElementPresent(proWait, By.name("login")).submit();
        assertTrue(waitForTextToAppear(proWait, "on virheellinen"));
    }

    @Test
    public void proLoginInvalid() {
        waitElementPresent(proWait, By.name("username")).sendKeys("Hakkeri");
        waitElementPresent(proWait, By.name("password")).sendKeys("heikki");
        waitElementPresent(proWait, By.name("login")).submit();
        assertTrue(waitForTextToAppear(proWait, "on virheellinen"));
    }

    /**
     * As a professional I want to logout
     */
    @Test
    public void proLogoutTest() {
        proLogin(proWait);
        proLogout(proWait);
        assertTrue(waitForTextToAppear(proWait, "Salasana:"));
    }



}

