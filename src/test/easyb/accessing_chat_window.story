import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver

description 'As a user I want to access a chat window'

scenario "User cannot see the chat window if she/he has not been picked from a pool by a healthcare professional", {
    given 'user enters the chat page', {
            driver = new FirefoxDriver()
            driver.get("http://localhost:8080")
    }
    when 'a starting message is submitted', {
            element = driver.findElement(By.id("username"))
            element.sendKeys("Matti")
            element = driver.findElement(By.id("startMessage"))
            element.sendKeys("Moikka!")
            element = driver.findElement(By.tagName("button"))
            element.submit()
    }
    then 'a queueing view is showed to the user', {
            page = driver.getPageSource()
            page.contains("Olet jonossa").shouldBe true
            page.contains("panel-body chat-body").shouldBe false
            page.contains("messageForm").shouldBe false
            page.contains("Moikka!").shouldBe false
    }
}

scenario "User can see a chat window when she/he is picked from a pool of customers by a healthcare professional", {
        given 'user has entered the chat page and submitted a starting message', {
        }
        and 'a professional has logged in', {
            proDriver = new FirefoxDriver()
            proDriver.get("http://localhost:8080/proCP.html")
            element = proDriver.findElement(By.name("username"))
            element.sendKeys("Hoitaja")
            element = proDriver.findElement(By.name("password"))
            element.sendKeys("salasana")
            element = proDriver.findElement(By.cssSelector("input[type='submit'][value='Sign In']"))
            element.submit()
        }
        when 'a professional chooses the started conversation from a pool', {
            element = proDriver.findElement(By.name("next")
            element.click()
        }
        then 'a chat window is opened for the user', {
            Thread.sleep(2000)
            page = driver.getPageSource()
            page.contains("panel-body chat-body").shouldBe true
            page.contains("messageForm").shouldBe true
            page.contains("Moikka!").shouldBe true
            page.contains("panel panel-default userToPool-panel").shouldBe false
            page.contains("Aloitusviesti:").shouldbe false
            driver.quit()
        }
}

