import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.*;
import java.util.concurrent.TimeUnit

description 'As a user I want to see the messages other people have sent to discussion'

scenario "user can view a message the other party has sent to the discussion", {
    given 'a customer and a professional have accessed the chat window', {
        custdr = new FirefoxDriver()
        wait = new WebDriverWait(custdr, 7)
        custdr.get("http://localhost:8080")
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")))
        element.sendKeys("Matti")
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("startMessage")))
        element.sendKeys("Moikka!")
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("button")))
        element.submit()
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
        element.click()
        }
    when 'the other person sends a message', {
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("messageArea")))
        element.sendKeys("Can you see this message?")
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("send")))
        element.submit()
    }
    then 'user can view it in the chat window', {
        Thread.sleep(3000)
        page = prodr.getPageSource()
        page.contains("Can you see this message?").shouldBe true
        custdr.quit()
        prodr.quit()
    }
}
/*
scenario "user can view messages sent by both parties in time order", {
    given 'two persons have accessed the chat window', {
        firstdr = new FirefoxDriver()
        firstdr.get("http://localhost:8080")
        seconddr = new FirefoxDriver()
        seconddr.get("http://localhost:8080")
    }
    when 'the other person sends multiple messages', {
        firste = firstdr.findElement(By.name("messageArea"))
        seconde = seconddr.findElement(By.name("messageArea"))
        firstb = firstdr.findElement(By.name("send"))
        secondb = seconddr.findElement(By.name("send"))
        firste.sendKeys("1")
        firstb.submit()
        seconde.sendKeys("2")
        secondb.submit()
        firste.sendKeys("3")
        firstb.submit()
    }
    then 'user can view all the messages in a time order', {
        Thread.sleep(1000)
        elements = seconddr.findElements(By.name("messageText"))
        first = elements.first().getText()
        last = first.toInteger()
        for(e in elements){
            this = e.getText().toInteger()
            last.shouldBeLessThan this
            last = this
        }
        firstdr.quit()
        seconddr.quit()
    }
}
*/