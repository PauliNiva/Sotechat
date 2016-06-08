import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.*;

description 'As a user, I want to join the queue to wait my turn'

scenario "user joins a common queue when accessing chat", {
    given 'user has accessed chat page', {
        driver = new FirefoxDriver()
        wait = new WebDriverWait(driver, 7)
        driver.get("http://localhost:8080")
    }
    when 'username and a starting message is submitted', {
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")))
        element.sendKeys("Leila")
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("startMessage")))
        element.sendKeys("Moikka!")
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("button")))
        element.submit()
    }
    then 'a queueing view is showed to the user', {
         Thread.sleep(5000)
         page = driver.getPageSource()
         page.contains("Olet jonossa").shouldBe true
    }
    and 'user is added to the pool of customers professionals side', {
         prodr = new FirefoxDriver()
         wait2 = new WebDriverWait(prodr, 7)
         prodr.get("http://localhost:8080/proCP.html")
         element = wait2.until(ExpectedConditions.presenceOfElementLocated(By.name("username")))
         element.sendKeys("Hoitaja")
         element = wait2.until(ExpectedConditions.presenceOfElementLocated(By.name("password")))
         element.sendKeys("salasana")
         element = wait2.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='submit'][value='Sign In']")))
         element.submit()
         element = wait2.until(ExpectedConditions.elementToBeClickable(By.name("next")))
         page = prodr.getPageSource()
         page.contains("Leila").shouldBe true
         element.click()
         driver.quit()
         prodr.quit()
    }
}
