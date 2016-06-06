
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import java.util.concurrent.TimeUnit

description 'As a user I want to see the messages other people have sent to discussion'

scenario "user can view a message the other party has sent to the discussion", {
    given 'a customer and a professional have accessed the chat window', {
        custdr = new FirefoxDriver()
        custdr.get("http://localhost:8080")
        element = custdr.findElement(By.id("username"))
        element.sendKeys("Matti")
        element = custdr.findElement(By.id("startMessage"))
        element.sendKeys("Moikka!")
        element = custdr.findElement(By.tagName("button"))
        element.submit()
        prodr = new FirefoxDriver()
        prodr.get("http://localhost:8080/proCP.html")
        element = prodr.findElement(By.name("username"))
        element.sendKeys("Hoitaja")
        element = prodr.findElement(By.name("password"))
        element.sendKeys("salasana")
        element = prodr.findElement(By.cssSelector("input[type='submit'][value='Sign In']"))
        element.submit()
        element = prodr.findElement(By.name("next")
        element.click()
        }
    when 'the other person sends a message', {
        Thread.sleep(2000)
        element = custdr.findElement(By.name("messageArea"))
        element.sendKeys("Can you see this message?")
        element = custdr.findElement(By.name("send"))
        element.submit()
    }
    then 'user can view it in the chat window', {
        Thread.sleep(1000)
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