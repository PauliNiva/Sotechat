package sotechatIT;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Selenium webdriverin käyttöä nopeuttavia funktioita hyväksymätestejä varten
 */
public final class sotechatITCommands {

    /**
     * Osoitteet, joista löytyvät asiakkaan ja ammitlaisen näkymä
     */
    public static final String CUSTOMERADDRESS = "http://localhost:8080";
    public static final String PROADDRESS = "http://localhost:8080/pro";

    /**
     * Täyttää käyttäjänimen ja aloitusviestin ja lähettää sen serverille
     *
     * @param wait Odottavaan webdriveriin kiinnitetty WebDriverWait
     */
    public static void waitAndFillInformation(WebDriverWait wait) {
        waitElementPresent(wait, By.id("username")).sendKeys("Testi");
        waitElementPresent(wait, By.id("startMessage")).sendKeys("Moikkamoi!");
        waitElementPresent(wait, By.tagName("button")).click();
    }

    /**
     * Odottaa kunnes käyttäjä näkee jonon
     *
     * @param wait Odottavaan webdriveriin kiinnitetty WebDriverWait
     * @return Jonotus elementin
     */
    public static WebElement waitQueueWindowsAppear(WebDriverWait wait) {
        return waitElementPresent(wait, By.id("userInQueue"));
    }

    /**
     * Odottaa kunnes chat-ikkuna näkyy
     *
     * @param wait Odottavaan webdriveriin kiinnitetty WebDriverWait
     * @return Viestinkirjoitus kentän elementin
     */
    public static WebElement waitChatWindowsAppear(WebDriverWait wait) {
        return waitElementPresent(wait, By.name("messageArea"));
    }

    /**
     * Lähettää viestin chatti ruutuunm kun ruutu havaitaan
     *
     * @param wait    Odottavaan webdriveriin kiinnitetty WebDriverWait
     * @param message viesti joka lähetetään chattiin
     */
    public static void sendMessageChatWindow(WebDriverWait wait, String message) {
        waitElementPresent(wait, By.name("messageArea")).sendKeys(message);
        waitElementPresent(wait, By.name("send")).submit();
    }

    /**
     * Kirjauttaa hoitajan sisään järjestelmään, kun login tulee näkyviin
     *
     * @param wait Odottavaan webdriveriin kiinnitetty WebDriverWait
     */
    public static void proLogin(WebDriverWait wait) {
        waitElementPresent(wait, By.name("username")).sendKeys("Hoitaja");
        waitElementPresent(wait, By.name("password")).sendKeys("salasana");
        waitElementPresent(wait, By.name("login")).submit();
    }

    /**
     * Odottaa kunnes jonosta voi nostaa seuraavan ja sitten nostaa
     *
     * @param wait Odottavaan webdriveriin kiinnitetty WebDriverWait
     */
    public static void waitAndPickFromQueue(WebDriverWait wait) {
        waitElementClickable(wait, By.name("next")).click();
    }

    /**
     * Odotetaan elementtiä kunnes sitä voi klikata
     *
     * @param wait Odottavaan webdriveriin kiinnitetty WebDriverWait
     * @param by   By ehto jonka avulla odotetaan clikattavaa elementtiä
     * @return Elementti kun klikattavissa
     */
    public static WebElement waitElementClickable(WebDriverWait wait, By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    /**
     * Odotetaan että hakuehdoilal löytyvä elementti ilmestyy sivulle
     *
     * @param wait Odottavaan webdriveriin kiinnitetty WebDriverWait
     * @param by   By ehto jonka avulla odotetaan määrättyä elementtiä
     * @return Elementti kun löytyy
     */
    public static WebElement waitElementPresent(WebDriverWait wait, By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public static WebElement waitVisibilityOfElement(WebDriverWait wait, By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * @param wait         Odottavaan webdriveriin kiinnitetty WebDriverWait
     * @param textToAppear Teksti jonka oletetaan ilmestyvän sivulle
     * @return true kun teksti löytyy, muuten timeout
     */
    public static boolean waitForTextToAppear(WebDriverWait wait, String textToAppear) {
        By byXpath = By.xpath("//*[contains(text(),'" + textToAppear + "')]");
        wait.until(ExpectedConditions.presenceOfElementLocated(byXpath));
        return true;
    }

}
