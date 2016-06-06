/*
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import sotechat.Application

description 'As a user I want to access a chat window'

scenario "New user is shown the start page", {
    given 'User is new', {
        app = new Application()
        driver = new FirefoxDriver()
    }
    when 'User goes to chat url', {
        driver.get("http://localhost:8080")
    }
    then 'The start page is shown', {
        Thread.sleep(5000)
        page = driver.getPageSource()
        page.contains("Aloitusviesti").shouldBe true
        page.contains("Nimimerkki").shouldBe true
        driver.quit()
    }
}

scenario "Queuing user is shown the queue page", {
    given 'User\'s status is queue', {
        app = new Application()
        driver = new FirefoxDriver()
    }
    when 'User goes to chat url', {
        driver.get("http://localhost:8080")
        Thread.sleep(1000)
        HttpSession session = app.getSessionRepo().getLatestSession()
        session.setAttribute("state", "queue");
        Thread.sleep(1000)
        driver.get("http://localhost:8080")
    }
    then 'A queueing window is shown', {
        Thread.sleep(3000)
        page = driver.getPageSource()
        page.contains("Jonossa").shouldBe true
        driver.quit()
    }
}
*/