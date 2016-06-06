import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver

description 'As a user, I want to join the queue to wait my turn'

scenario "user joins a common queue when accessing chat", {
    given 'user has accessed chat page', {
        driver = new FirefoxDriver()
        driver.get("http://localhost:8080")
    }
    when 'username and a starting message is submitted', {
        element = driver.findElement(By.id("username"))
        element.sendKeys("Leila")
        element = driver.findElement(By.id("startMessage"))
        element.sendKeys("Moikka!")
        element = driver.findElement(By.tagName("button"))
        element.submit()
    }
    then 'a queueing view is showed to the user', {
         Thread.sleep(2000)
         page = driver.getPageSource()
         page.contains("Olet jonossa").shouldBe true
    }
    and 'user is added to the pool of customers professionals side', {
         prodr = new FirefoxDriver()
         prodr.get("http://localhost:8080/proCP.html")
         element = prodr.findElement(By.name("username"))
         element.sendKeys("Hoitaja")
         element = prodr.findElement(By.name("password"))
         element.sendKeys("salasana")
         element = prodr.findElement(By.cssSelector("input[type='submit'][value='Sign In']"))
         element.submit()
         Thread.sleep(2000)
         page = prodr.getPageSource()
         page.contains("Leila").shouldBe true
         element = prodr.findElement(By.name("next"))
         element.click()
         driver.quit()
         prodr.quit()
    }
}
