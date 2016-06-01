import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver

description 'As a professional, I want to see control panel'

scenario "a professional can view a control panel for chat when logged in", {
        given 'professional´s page is accessed', {
            driver = new FirefoxDriver()
            driver.get("https://localhost:8080/proCP.html")
        }
        when 'professional logs in with proper username and password', {
            element = driver.findElement(By.name("username"))
            element.sendKeys("Hoitaja")
            element = driver.findElement(By.name("password"))
            element.sendKeys("salasana")
            element = driver.findElement(By.cssSelector("input[type='submit'][value='Sign In']"))
            element.submit()
        }
        then 'a control panel can be viewed', {
            driver.getCurrentUrl().shouldEqual "https://localhost:8080/proCP.html"
            page = driver.getPageSource()
            page.contains("proCPController").shouldBe true
            page.contains("chat-container ").shouldBe true
            driver.quit()
        }
}

scenario "a professional can't see a control panel for chat when not logged in", {
        given 'professional´s page is accessed', {
            driver = new FirefoxDriver()
            driver.get("https://localhost:8080/proCP.html")
        }
        when 'user has not logged in with proper username and password', {
            element = driver.findElement(By.cssSelector("input[type='submit'][value='Sign In']"))
            element.submit()
        }
        then 'control panel is not visible to the user', {
            driver.getCurrentUrl().shouldEqual "https://localhost:8080/login?error"
            page = driver.getPageSource()
            page.contains("Väärä käyttäjätunnus tai salasana.").shouldBe true
            driver.quit()
        }
}