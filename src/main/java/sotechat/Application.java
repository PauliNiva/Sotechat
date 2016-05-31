package sotechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Käynnistää palvelimen, oletuksena http://localhost:8080 .
 */
@SpringBootApplication
public class Application {

    /**
     * Käynnistää palvelimen.
     * @param args Ei käytetä argumentteja.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
