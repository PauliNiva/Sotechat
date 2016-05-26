
import org.openqa.selenium.*
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.firefox.FirefoxDriver

description 'As a user I want to send a message'
/*
scenario "user cand write a message on a text input", {
    given 'user has chosen the right link', {
        driver = new FirefoxDriver()
    }
    when 'a chat window is accessed', {
        driver.get("http://localhost:8080")
    }
    then 'text can be applied to a text field', {
        element = driver.findElementByTagName(textarea)
        element.shouldBeA WebElement
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
        element.getText().shouldEqual ""
    }
}
*/
scenario "user can send the message he or she has written to the server by pressing enter", {
    given 'text is written to the right text field in a chat window'
    when 'enter is pressed'
    then 'the text can be sent to the chat server'
}

scenario "user can't send an invalid message", {
    given 'nothing is written to the text field'
    when 'submit is clicked'
    then 'no message is sent'
}
