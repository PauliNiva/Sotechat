package sotechat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sotechat.data.SessionRepo;

/** Kaynnistaa palvelimen, oletuksena http://localhost:8080 .
 */
@SpringBootApplication
public class Application {

    /**
     * Yritys saada testeihin nakyvyytta sessionRepoon.
     */
    @Autowired
    private SessionRepo sessionRepo;

    /**
     * Kaynnistaa palvelimen.
     * @param args Ei kayteta argumentteja.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Yritys saada testeihin nakyvyytta sessionRepoon.
     * @return sessionRepo.
     */
    public SessionRepo getSessionRepo() {
        return this.sessionRepo;
    }
}
