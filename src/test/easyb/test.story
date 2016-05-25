
import org.openqa.selenium.*
import org.openqa.selenium.htmlunit.HtmlUnitDriver

description 'As a user I want to view the messages I have sent in the chat window'

scenario "user can see a message that has been sent to the server", {
        given 'a message has been written to the right text field in the chat window', {
                driver = new HtmlUnitDriver(true)
                driver.get("http://localhost:8080")
                element = driver.findElement(By.name("messageArea"))
                element.sendKeys("I want to send this message")
        }
        when 'submit button is pressed', {
                element = driver.findElement(By.name("send"))
                element.submit()
        }
        then 'the message appears in the chat window', {
                driver.getPageSource().contains("I want to send this message")
        }
}

scenario "user can see the time when a message has been sent", {
        given 'a message has been written', {
                driver = new HtmlUnitDriver(true)
                driver.get("http://localhost:8080")
                element = driver.findElement(By.name("messageArea"))
                element.sendKeys("message with time")
        }
        when 'the message is submitted', {
                element = driver.findElement(By.name("send"))
                time = DateTime("HH:MM")
                element.submit()
                time.plus(Period.seconds(30))
        }
        then 'the sending time of the message can be seen', {
                driver.getPageSource().contains(time)
        }
}

scenario "user can view multiple messages he or she has sent in a time order", {
        given 'the chat window is accessed', {
                driver = new HtmlUnitDriver(true)
                driver.get("http://localhost:8080")
        }
        when 'multiple messages are sent', {
                element = driver.findElement(By.name("messageArea"))
        }
        then 'they can be viewed in a time order'
}
