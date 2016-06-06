
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import java.util.List
import java.lang.Integer


description 'As a user I want to view the messages I have sent in the chat window'

scenario "user can see a message that has been sent to the server", {
        given 'a chat window is accessed', {
                driver = new FirefoxDriver()
                driver.get("http://localhost:8080")
                element = driver.findElement(By.id("username"))
                element.sendKeys("Matti")
                element = driver.findElement(By.id("startMessage"))
                element.sendKeys("Moikka!")
                element = driver.findElement(By.tagName("button"))
                element.submit()
                prodr = new FirefoxDriver()
                prodr.get("http://localhost:8080/proCP.html")
                element = prodr.findElement(By.name("username"))
                element.sendKeys("Hoitaja")
                element = prodr.findElement(By.name("password"))
                element.sendKeys("salasana")
                element = prodr.findElement(By.cssSelector("input[type='submit'][value='Sign In']"))
                element.submit()
                Thread.sleep(2000)
                element = prodr.findElement(By.name("next"))
                element.click()
        }
        when 'a message has been written to the right text field in the chat window', {
                element = driver.findElement(By.name("messageArea"))
                element.sendKeys("I want to send this message")
        }
        and 'submit button is pressed', {
                button = driver.findElement(By.name("send"))
                button.submit()
        }
        then 'the message appears in the chat window', {
                Thread.sleep(2000)
                element = driver.findElement(By.name("messageArea"))
                element.getText().equals("")shouldBe true
                driver.getPageSource().contains("I want to send this message").shouldBe true
                driver.quit()
                prodr.quit()
        }
}

/*
scenario "user can view multiple messages he or she has sent in a time order", {
        given 'the chat window is accessed', {
                driver = new FirefoxDriver()
                driver.get("http://localhost:8080")
        }
        when 'multiple messages are sent', {
                element = driver.findElement(By.name("messageArea"))
                button = driver.findElement(By.name("send"))
                element.sendKeys("1")
                button.submit()
                element.sendKeys("2")
                button.submit()
                element.sendKeys("3")
                button.submit()
        }
        then 'they can be viewed in a time order', {
                Thread.sleep(1000)
                driver.get("http://localhost:8080")
                elements = driver.findElements(By.name("messageText"))
                first = elements.first().getText()
                last = first.toInteger()
                for(e in elements){
                    this = e.getText().toInteger()
                      last.shouldBeLessThan this
                    last = this
                }

        }
}
*/
