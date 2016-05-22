package sotechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Käynnistää Chat-webappin.
 */
@SpringBootApplication
public final class Application {

    /**
     * Tyhjä konstruktori.
     */
    private Application() {
        //not called
    }

    /**
     * Käynnistää sovelluksen.
     * @param args Komentoriviargumentit taulukkona merkkijono-olioita.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
