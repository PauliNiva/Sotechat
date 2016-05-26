
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver

description 'As a user I want to send a message'

scenario "user cand write a message on a text input", {
    given 'user has chosen the right link', {
        driver = new FirefoxDriver()
    }
    when 'a chat window is accessed', {
        driver.get("http://localhost:8080")
    }
    then 'text can be applied to a text field', {
        page = driver.getPageSource()
        page.contains("textarea").shouldBe true
        page.contains("messageArea").shouldBe true
        page.contains("sendMessage();").shouldBe true
        page.contains("button").shouldBe true
        page.contains("submit").shouldBe true
        page.contains("Lähetä").shouldBe true
    }
}

scenario "user can send the message he or she has written to the server by pressing submit", {
    given 'text is written to the right text field in a chat window', {
        driver = new FirefoxDriver()
        driver.get("http://localhost:8080")
        element = driver.findElement(By.name("messageArea"))
        element.sendKeys("my first testmessage")
    }
    when 'submit button is clicked', {
        element = driver.findElement(By.name("send"))
        element.submit();
    }
    then 'the text can be sent to the chat server', {
        element = driver.findElement(By.name("messageArea"))
//        element.getText().shouldEqual ""
        page = driver.getPageSource()
        page.contains("my first testmessage").shouldBe true
    }
}

scenario "user can send the message he or she has written to the server by pressing enter", {
    given 'text is written to the right text field in a chat window'
    when 'enter is pressed'
    then 'the text can be sent to the chat server'
}

scenario "user can't send an invalid message", {
    given 'nothing is written to the text field', {
            driver = new FirefoxDriver()
            driver.get("http://localhost:8080")
    }
    when 'submit is clicked', {
            element = driver.findElement(By.name("send"))
            element.submit();
    }
    then 'no message is sent', {
            driver.getPageSource().contains("panel panel-default message-panel").shouldBe false
    }
}
