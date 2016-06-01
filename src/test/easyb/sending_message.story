
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.Keys

description 'As a user I want to send a message'

scenario "user cand write a message on a text input", {
    given 'user has chosen the right link', {
        driver = new FirefoxDriver()
    }
    when 'a chat window is accessed', {
        driver.get("https://localhost:8080")
    }
    then 'text can be applied to a text field', {
        page = driver.getPageSource()
        page.contains("textarea").shouldBe true
        page.contains("messageArea").shouldBe true
        page.contains("sendMessage();").shouldBe true
        page.contains("button").shouldBe true
        page.contains("submit").shouldBe true
    }
}

scenario "user can send the message he or she has written to the server by pressing submit", {
    given 'text is written to the right text field in a chat window', {
        element = driver.findElement(By.name("messageArea"))
        element.sendKeys("my first testmessage")
    }
    when 'submit button is clicked', {
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

scenario "user can't send an empty message", {
    given 'nothing is written to the text field', {
            driver = new FirefoxDriver()
            driver.get("https://localhost:8080")
    }
    when 'submit is clicked', {
            element = driver.findElement(By.name("messageArea"))
            element.sendKeys("")
            element = driver.findElement(By.name("send"))
            element.submit();
    }
    then 'no message is sent', {
            Thread.sleep(1000)
            page = driver.getPageSource()
            page.contains("panel panel-default message-panel").shouldBe false
            page.contains("messageText").shouldBe false
            driver.quit()
    }
}
