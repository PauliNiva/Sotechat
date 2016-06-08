import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.*;

description 'As a professional, I want to pick a client from the visible queue'

scenario "professional can pick the first client from a pool of chats to start a conversation", {
    given 'professional has logged in', {
         prodr = new FirefoxDriver()
         wait = new WebDriverWait(prodr, 7)
         prodr.get("http://localhost:8080/proCP.html")
         element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")))
         element.sendKeys("Hoitaja")
         element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")))
         element.sendKeys("salasana")
         element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='submit'][value='Sign In']")))
         element.submit()
    }
    and 'a customer has accessed a chat window', {
        driver = new FirefoxDriver()
        wait2 = new WebDriverWait(driver, 7)
        driver.get("http://localhost:8080")
        element = wait2.until(ExpectedConditions.presenceOfElementLocated(By.id("username")))
        element.sendKeys("Matti")
        element = wait2.until(ExpectedConditions.presenceOfElementLocated(By.id("startMessage")))
        element.sendKeys("Moikka!")
        element = wait2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("button")))
        element.submit()
        }
    when 'professional click´s next in line button', {
        element = wait.until(ExpectedConditions.elementToBeClickable(By.name("next")))
        element.click()
        }
    then 'a chat window is opened that has a connection to the customer', {
        Thread.sleep(2000)
        page = prodr.getPageSource()
        page.contains("panel-body chat-body").shouldBe true
        driver.quit()
        prodr.quit()
    }
}

/* TODO: Ei implementoitu kayttoliittymaan
scenario "professional can pick a customer of her or his choosing from the chat pool to start a conversation", {
    given 'professional has logged in', {
    }
    and 'a customer has accessed a chat window', {
        driver = new FirefoxDriver()
        wait = new WebDriverWait(driver, 7)
        driver.get("http://localhost:8080")
        element = driver.findElement(By.id("username"))
        element.sendKeys("Liisa")
        element = driver.findElement(By.id("startMessage"))
        element.sendKeys("Moi!")
        element = driver.findElement(By.tagName("button"))
        element.submit()
        prodr = new FirefoxDriver()
        wait2 = new WebDriverWait(prodr, 3)
        prodr.get("http://localhost:8080/proCP.html")
        element = prodr.findElement(By.name("username"))
        element.sendKeys("Hoitaja")
        element = prodr.findElement(By.name("password"))
        element.sendKeys("salasana")
        element = prodr.findElement(By.cssSelector("input[type='submit'][value='Sign In']"))
        element.submit()
        }
    }
    when 'professional click´s a button next to customers name', {
        Thread.sleep(2000)
        element = prodr.findElement(By.name("Liisa"))
        element.click()
    }
    then 'a chat window is opened tha has a connection to the chosen customer', {
        page = prodr.getPageSource()
        page.contains("panel-body chat-body").shouldBe true
        driver.quit()
        prodr.quit()
    }
}
*/