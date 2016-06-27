package integrationTests.util;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.HashMap;

import static com.github.webdriverextensions.Bot.*;
import static integrationTests.util.sotechatITCommands.PROADDRESS;
import static integrationTests.util.sotechatITCommands.proLogin;


@RunWith(WebDriverRunner.class)
@Chrome
public class DriverHandler {

    private HashMap<String, WebDriver> drivers;
    private HashMap<String, WebDriverWait> waitDrivers;
    private WebDriver openChatdriver;


    public DriverHandler(String... driverNames) {
        String path = buildPath(
                "src", "test", "resources", "firefox", "firefox");
        drivers = new HashMap<>();
        waitDrivers = new HashMap<>();
        openChat();
        addDrivers(driverNames);

    }

    public WebDriver addDriver(String name) {
        WebDriver driver = new ChromeDriver();
        WebDriverWait waitDriver = new WebDriverWait(driver, 7);
        drivers.put(name, driver);
        waitDrivers.put(name, waitDriver);
        return driver;
    }

    public void openChat() {
        openChatdriver = new ChromeDriver();
        openChatdriver.get(PROADDRESS);
        WebDriverWait waitDriver = new WebDriverWait(openChatdriver, 7);
        proLogin(waitDriver);
    }

    public void addDrivers(String... names) {
        for (String name : names) {
            addDriver(name);
        }
    }

    public void closeAll() {
        openChatdriver.quit();
        for (WebDriver driver : drivers.values()) {
            driver.quit();
        }
    }

    public void HttpGet(String driverName, String path) {
        drivers.get(driverName).get(path);
    }

    public WebDriver getDriver(String name) {
        return drivers.get(name);
    }

    public WebDriverWait getWaitDriver(String name) {
        return waitDrivers.get(name);
    }

    private static String buildPath(String... items) {
        String path = System.getProperty("user.dir");
        for (String item : items) {
            path += File.separator;
            path += item;
        }
        return path;
    }
}
