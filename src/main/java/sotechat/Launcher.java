package sotechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Luokka <code>Spring</code>-sovelluksen kaynnistamiseen.
 */
@SpringBootApplication
public class Launcher {

    /**
     * Kaynnistaa <code>Spring</code>-sovelluksen.
     * @param args Komentoriviargumentit.
     * @return Palauttaa <code>ApplicationContext</code>-olion.
     */
    public ApplicationContext launch(final String[] args) {
        return SpringApplication.run(Launcher.class, args);
    }

}
