import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.Keys

description 'As a user I want to send a message'

scenario "user cand write a message on a text input", {
    given 'user has accessed the chat page', {
        driver = new FirefoxDriver()
        wait = new WebDriverWait(driver, 7)
        driver.get("http://localhost:8080")
    }
    when 'a starting message has been submitted', {
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")))
        element.sendKeys("Matti")
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("startMessage")))
        element.sendKeys("Moikka!")
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("button")))
        element.submit()
    }
    and 'a professional has picked the user from a pool', {
        proDriver = new FirefoxDriver()
        wait2 = new WebDriverWait(proDriver, 7)
        proDriver.get("http://localhost:8080/proCP.html")
        element = wait2.until(ExpectedConditions.presenceOfElementLocated(By.name("username")))
        element.sendKeys("Hoitaja")
        element = wait2.until(ExpectedConditions.presenceOfElementLocated(By.name("password")))
        element.sendKeys("salasana")
        element = wait2.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='submit'][value='Sign In']")))
        element.submit()
        element = wait2.until(ExpectedConditions.elementToBeClickable(By.name("next")))
        element.click()
    }
    then 'text can be applied to a text field', {
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("messageArea")))
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
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("messageArea")))
        element.sendKeys("my first testmessage")
    }
    and 'submit button is clicked', {
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("send")))
        element.submit();
    }
    then 'the text can be sent to the chat server', {
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("messageArea")))
        element.getText().equals("").shouldBe true
        page = driver.getPageSource()
        page.contains("my first testmessage").shouldBe true
    }
}

scenario "user can send the message he or she has written to the server by pressing enter", {
    given 'text is written to the right text field in a chat window', {
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("messageArea")))
        element.sendKeys("my second testmessage")
    }
    when 'enter is pressed', {
        driver.getKeyboard().pressKey(Keys.ENTER)
    }
    then 'the text can be sent to the chat server', {
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("messageArea")))
        element.getText().equals("").shouldBe true
        page = driver.getPageSource()
        page.contains("my second testmessage").shouldBe true
        driver.quit()
        proDriver.quit()
    }
}

