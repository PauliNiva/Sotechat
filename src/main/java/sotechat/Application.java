package sotechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


public class Application {

    private static Launcher launcher = new Launcher();

    /**
     * Kaynnistaa palvelimen.
     * @param args Ei kayteta argumentteja.
     */
    public static void main(final String[] args) {
        launcher.launch(args);

    }

    static void setLauncher(Launcher plauncher) {
        Application.launcher = plauncher;
    }
}
