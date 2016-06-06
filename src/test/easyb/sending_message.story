
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.Keys

description 'As a user I want to send a message'

scenario "user cand write a message on a text input", {
    given 'user has accessed the chat page', {
        driver = new FirefoxDriver()
        driver.get("http://localhost:8080")
    }
    when 'a starting message has been submitted', {
        element = driver.findElement(By.id("username"))
        element.sendKeys("Matti")
        element = driver.findElement(By.id("startMessage"))
        element.sendKeys("Moikka!")
        element = driver.findElement(By.tagName("button"))
        element.submit()
    }
    and 'a professional has picked the user from a pool', {
        proDriver = new FirefoxDriver()
        proDriver.get("http://localhost:8080/proCP.html")
        element = proDriver.findElement(By.name("username"))
        element.sendKeys("Hoitaja")
        element = proDriver.findElement(By.name("password"))
        element.sendKeys("salasana")
        element = proDriver.findElement(By.cssSelector("input[type='submit'][value='Sign In']"))
        element.submit()
        element = proDriver.findElement(By.name("next")
        element.click()
    }
    then 'text can be applied to a text field', {
        Thread.sleep(1000)
        page = driver.getPageSource()
        page.contains("textarea").shouldBe true
        page.contains("messageArea").shouldBe true
        page.contains("sendMessage();").shouldBe true
        page.contains("button").shouldBe true
        page.contains("submit").shouldBe true
    }
}

scenario "user can send the message he or she has written to the server by pressing submit", {
    given 'a chat window is accessed', {
    }
    when 'text is written to the text field in a chat window', {
        element = driver.findElement(By.name("messageArea"))
        element.sendKeys("my first testmessage")
    }
    and 'submit button is clicked', {
        element = driver.findElement(By.name("send"))
        element.submit();
    }
    then 'the text can be sent to the chat server', {
        Thread.sleep(1000)
        element = driver.findElement(By.name("messageArea"))
        element.getText().equals("").shouldBe true
        page = driver.getPageSource()
        page.contains("my first testmessage").shouldBe true
    }
}

scenario "user can send the message he or she has written to the server by pressing enter", {
    given 'text is written to the right text field in a chat window', {
        element = driver.findElement(By.name("messageArea"))
        element.sendKeys("my second testmessage")
    }
    when 'enter is pressed', {
        driver.getKeyboard().pressKey(Keys.ENTER)
    }
    then 'the text can be sent to the chat server', {
        Thread.sleep(1000)
        element = driver.findElement(By.name("messageArea"))
        element.getText().equals("").shouldBe true
        page = driver.getPageSource()
        page.contains("my second testmessage").shouldBe true
        driver.quit()
    }
}

