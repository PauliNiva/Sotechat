package it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import sotechat.Application;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:8081")
public class accessingChatWindowIT {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, 7);
        driver.get("http://localhost:8081");
    }

    @After
    public void tearDown() throws Exception {
        driver.close();
    }

    @Test
    public void testi() {
        waitElementPresent(By.id("username")).sendKeys("Eero");
        waitElementPresent(By.id("startMessage")).sendKeys("Moikkamoi!");
        waitElementPresent(By.tagName("button")).click();
        waitElementPresent(By.id("userInQueue"));
        String page = driver.getPageSource();
        assertTrue(page.contains("Olet jonossa"));
        assertFalse(page.contains("panel-body chat-body"));
        assertFalse(page.contains("messageForm"));
    }

    private WebElement waitElementPresent(By a) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(a));
    }

}

