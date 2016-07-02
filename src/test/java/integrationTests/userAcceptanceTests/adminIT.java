package integrationTests.userAcceptanceTests;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;


import static org.junit.Assert.*;
import static integrationTests.util.sotechatITCommands.*;

/**
 * As a admin, I want manage users.
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class adminIT {

    private DriverHandler handler;
    private WebDriverWait proWait;

    @Before
    public void setUp() throws Exception {
        handler = new DriverHandler("pro");
        handler.HttpGet("pro", PROADDRESS);
        proWait = handler.getWaitDriver("pro");
        waitElementPresent(proWait, By.name("username")).sendKeys("admin");
        waitElementPresent(proWait, By.name("password")).sendKeys("0000");
        waitElementPresent(proWait, By.name("login")).submit();
    }

    @After
    public void tearDown() throws Exception {
        handler.closeAll();
    }

    @Test
    public void adminCanCreateNewUser() {
        waitElementPresent(proWait, By.name("newUserButton"));
        waitElementClickable(proWait, By.name("newUserButton")).click();
        waitElementPresent(proWait, By.name("loginname")).sendKeys("testi");
        waitElementPresent(proWait, By.name("username")).sendKeys("testi1");
        waitElementPresent(proWait, By.name("password")).sendKeys("tasti2");
        waitElementClickable(proWait, By.name("createNewUserBtn")).click();
        assertTrue(waitForTextToAppear(proWait,"Toiminto onnistui!"));
    }


    @Test
    public void adminCanDeleteUser() {
        waitElementPresent(proWait, By.name("newUserButton"));
        waitElementClickable(proWait, By.name("newUserButton")).click();
        waitElementPresent(proWait, By.name("loginname")).sendKeys("testi");
        waitElementPresent(proWait, By.name("username")).sendKeys("testi2");
        waitElementPresent(proWait, By.name("password")).sendKeys("tasti2");
        waitElementClickable(proWait, By.name("createNewUserBtn")).click();
        waitElementClickable(proWait, By.name("deleteUserBtntesti2")).click();
        waitElementPresent(proWait, By.name("sure")).click();
        assertTrue(waitForTextToAppear(proWait,"Toiminto onnistui!"));
    }



}

