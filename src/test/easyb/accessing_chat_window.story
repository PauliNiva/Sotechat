import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.*;

description 'As a user I want to access a chat window'

scenario "User cannot see the chat window if she/he has not been picked from a pool by a healthcare professional", {
    given 'user enters the chat page', {
            driver = new FirefoxDriver()
            wait = new WebDriverWait(driver, 3)
            driver.get("http://localhost:8080")
    }
    when 'a starting message is submitted', {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")))
            element.sendKeys("Eero")
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("startMessage")))
            element.sendKeys("Moikkamoi!")
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("button")))
            element.submit()
    }
    then 'a queueing view is showed to the user', {
            Thread.sleep(2000)
            page = driver.getPageSource()
            page.contains("Olet jonossa").shouldBe true
            page.contains("panel-body chat-body").shouldBe false
            page.contains("messageForm").shouldBe false
    }
}

scenario "User can see a chat window when she/he is picked from a pool of customers by a healthcare professional", {
        given 'user has entered the chat page and submitted a starting message', {
        }
        and 'a professional has logged in', {
            proDriver = new FirefoxDriver()
            waitPro = new WebDriverWait(proDriver, 3)
            proDriver.get("http://localhost:8080/proCP.html")
            element = waitPro.until(ExpectedConditions.presenceOfElementLocated(By.name("username")))
            element.sendKeys("Hoitaja")
            element = waitPro.until(ExpectedConditions.presenceOfElementLocated(By.name("password")))
            element.sendKeys("salasana")
            element = waitPro.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='submit'][value='Sign In']")))
            element.submit()
        }
        when 'a professional chooses the started conversation from a pool', {
            element = waitPro.until(ExpectedConditions.elementToBeClickable(By.name("next")))
            element.click()
        }
        then 'a chat window is opened for the user', {
            Thread.sleep(3000)
            page = driver.getPageSource()
            page.contains("panel-body chat-body").shouldBe true
            page.contains("panel panel-default userToPool-panel").shouldBe false
            page.contains("Aloitusviesti:").shouldBe false
            driver.quit()
            proDriver.quit()
        }
}

