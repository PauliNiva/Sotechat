import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.*;

description 'As a professional, I want to see control panel'

scenario "a professional can view a control panel for chat when logged in", {
        given 'professional´s page is accessed', {
            driver = new FirefoxDriver()
            wait = new WebDriverWait(driver, 3)
            driver.get("http://localhost:8080/proCP.html")
        }
        when 'professional logs in with proper username and password', {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")))
            element.sendKeys("Hoitaja")
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")))
            element.sendKeys("salasana")
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='submit'][value='Sign In']")))
            element.submit()
        }
        then 'a control panel can be viewed', {
            Thread.sleep(2000)
            driver.getCurrentUrl().shouldEqual "http://localhost:8080/proCP.html"
            page = driver.getPageSource()
            page.contains("proCPController").shouldBe true
            page.contains("chat-container ").shouldBe true
            driver.quit()
        }
}

scenario "a professional can't see a control panel for chat when not logged in", {
        given 'professional´s page is accessed', {
            driver = new FirefoxDriver()
            wait = new WebDriverWait(driver, 3)
            driver.get("http://localhost:8080/proCP.html")
        }
        when 'user has not logged in with proper username and password', {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='submit'][value='Sign In']")))
            element.submit()
        }
        then 'control panel is not visible to the user', {
            Thread.sleep(2000)
            driver.getCurrentUrl().shouldEqual "http://localhost:8080/login?error"
            page = driver.getPageSource()
            page.contains("tunnus tai salasana.").shouldBe true
            driver.quit()
        }
}