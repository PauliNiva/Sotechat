package sotechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Käynnistää Chat-webappin.
 */
@SpringBootApplication
public class Application {

    /**
     * Käynnistää sovelluksen.
     * @param args Komentoriviargumentit taulukkona merkkijono-olioita.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
