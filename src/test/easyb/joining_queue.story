import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver

description 'As a user, I want to join the queue to wait my turn'

scenario "user sees a start page when first accessing the chat page", {
    given 'program is running', {
        driver = new FirefoxDriver()
    }
    when 'user accesses the chat page', {
        driver.get("http://localhost:8080")
    }
    then 'starting page view is showed', {
        page = driver.getPageSource()
        page.contains("userToQueueCtrl").shouldBe true
    }
    and 'a username and a message can be submitted'
        page.contains("Nimimerkki:").shouldBe true
        page.contains("Aloitusviesti:").shouldBe true
        page.contains("username").shouldBe true
        page.contains("startMessage").shouldBe true
        page.contains("Siirry jonottamaan").shouldBe true
    }

scenario "user joins a common queue when accessing chat", {
    given 'user has accessed chat page', {
    }
    when 'username and a starting message is submitted', {
        element = driver.findElement(By.id("username"))
        element.sendKeys("Matti")
        element = driver.findElement(By.id("startMessage"))
        element.sendKeys("Moikka!")
        element = driver.findElement(By.tagName("button"))
        element.submit()
    }
    then 'a queueing view is showed to the user', {
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
         page = prodr.getPageSource()
         page.contains("Matti").shouldBe true
    }
}
