package sotechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Käynnistää palvelimen, oletuksena http://localhost:8080 .
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
