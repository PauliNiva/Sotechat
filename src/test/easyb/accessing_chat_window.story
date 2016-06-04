import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver

description 'As a user I want to access a chat window'

scenario "User can open a chat window", {
        given 'User is on a right page', {
            driver = new FirefoxDriver()
        }
        when 'a link is clicked', {
            driver.get("http://localhost:8080")
        }
        then 'a chat window is opened', {
            page = driver.getPageSource()
            page.contains("panel panel-default chat-panel").shouldBe true
            page.contains("chatController as chat").shouldBe true
            page.contains("panel-body chat-body").shouldBe true
            driver.quit()
        }
}

