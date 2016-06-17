package sotechat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Kiintopiste, josta UI tarkastaa onko käyttäjä kirjautunut.
 * Sekä lähettää kirjautumistiedot HTTPBasicAuthentikationin mukaan.
 * TODO: Jos palautetaan pricupal liikaa tietoa?
 * TODO: Jos ei niin ei saada palautettua käyttäjän roolia.
 */
@RestController
public class LoginController {

    /**
     * Liittää /auth polun kirjautumista ja sen tarkistamista varten.
     * @param user Security principal joka liittyy evästeesen.
     *             Null, jos ei kirjautuneena.
     * @return Käyttäjän nimen, jos kirjautunut, muuten tyhjä JSON.
     */
    @RequestMapping("/auth")
    @ResponseBody
    public String user(Principal user) {
        if (user != null) {
            return "{\"name\":\"" + user.getName() + "\"}";
        }
        return "{}";
    }

}
