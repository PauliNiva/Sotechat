import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver

description 'As a user I want to access a chat window'

scenario "User can open a chat window when she/he is picked from a pool of customers by a healthcare professional", {
        given 'user enters the chat page', {
            driver = new FirefoxDriver()
            driver.get("http://localhost:8080")
        }
        when 'a starting message is submitted', {

        }
        and 'a professional chooses the started conversation from a pool', {

        }
        then 'a chat window is opened for the user', {
            page = driver.getPageSource()
            page.contains("Aloitusviesti").shouldBe true
            page.contains("Nimimerkki").shouldBe true
            driver.quit()
        }
}

scenario "User cannot see the chat window if she/he has not been picked from a pool by a healthcare professional", {
    given 'user enters the chat page', {

    }
    when 'a starting message is submitted', {

    }
    then 'a queueing view is showed to the user', {

    }
}
