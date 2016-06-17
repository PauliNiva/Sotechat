package integrationTests.additionalTests;

import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import integrationTests.util.DriverHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static integrationTests.util.sotechatITCommands.*;
import static integrationTests.util.sotechatITCommands.sendMessageChatWindow;
import static integrationTests.util.sotechatITCommands.waitForTextToAppear;

/**
 * Run godtest 1000 times, hope to catch concurrency errors.
 */
@RunWith(WebDriverRunner.class)
@Chrome
public class concurrencyIT {

    @Test
    public void concurrencyTesting() throws Exception {
        fastGodIT god = new fastGodIT();
        for (int i=1; i<=1000; i++) {
            System.out.println("Starting i = " + i);
            god.typicalFlow();
        }

    }
}
