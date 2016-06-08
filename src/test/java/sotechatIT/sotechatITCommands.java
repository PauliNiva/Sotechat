package sotechatIT;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class sotechatITCommands {

    public static final String CUSTOMERADDRES = "http://localhost:8080";
    public static final String PROADDRES = "http://localhost:8080/pro";

    public static void waitAndFillInformation(WebDriverWait wait) {
        waitElementPresent(wait, By.id("username")).sendKeys("Testi");
        waitElementPresent(wait, By.id("startMessage")).sendKeys("Moikkamoi!");
        waitElementPresent(wait, By.tagName("button")).click();
    }

    public static WebElement waitQueueWindowsAppear(WebDriverWait wait) {
        return waitElementPresent(wait, By.id("userInQueue"));
    }

    public static WebElement waitChatWindowsAppear(WebDriverWait wait) {
        return waitElementPresent(wait, By.name("messageArea"));
    }

    public static void sendMessageChatWindow(WebDriverWait wait, String message) {
        waitElementPresent(wait, By.name("messageArea")).sendKeys(message);
        waitElementPresent(wait, By.name("send")).submit();
    }

    public static void proLogin(WebDriverWait wait) {
        waitElementPresent(wait,By.name("username")).sendKeys("Hoitaja");
        waitElementPresent(wait,By.name("password")).sendKeys("salasana");
        waitElementPresent(wait,By.cssSelector("input[type='submit'][value='Sign In']")).submit();
    }

    public static void waitAndPickFromQueue(WebDriverWait wait) {
        waitElementClickable(wait,By.name("next")).click();
    }

    public static WebElement waitElementClickable(WebDriverWait wait, By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    public static WebElement waitElementPresent(WebDriverWait wait, By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public static boolean waitForTextToAppear(WebDriverWait wait, String textToAppear) {
        By byXpath = By.xpath("//*[contains(text(),'"+ textToAppear +"')]");
        wait.until(ExpectedConditions.presenceOfElementLocated(byXpath));
        return true;
    }

}
