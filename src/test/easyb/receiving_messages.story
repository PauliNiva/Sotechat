
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver

description 'As a user I want to see the messages other people have sent to discussion'

scenario "user can view a message the other party has sent to the discussion", {
    given 'two persons have accessed the chat window', {
        firstdr = new FirefoxDriver()
        firstdr.get("http://localhost:8080")
        seconddr = new FirefoxDriver()
        seconddr.get("http://localhost:8080")
        }
    when 'the other person sends a message', {
        element = firstdr.findElement(By.name("messageArea"))
        element.sendKeys("Can you see this message?")
        element = firstdr.findElement(By.name("send"))
        element.submit()
    }
    then 'user can view it in the chat window', {
        page = seconddr.getPageSource()
        page.contains("Can you see this message?").shouldBe true
    }
}

scenario "user can view all messages the other party has sent in a time order", {
    given 'two persons have accessed the chat window'
    when 'the other person sends multiple messages'
    then 'user can view all the messages in a time order'
}
