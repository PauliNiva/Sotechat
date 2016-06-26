package sotechat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Kiintopiste, josta UI tarkastaa onko käyttäjä kirjautunut.
 * Sekä lähettää kirjautumistiedot HTTPBasicAuthentikationin mukaan.
 */
@RestController
public class LoginController {

    /**
     * Liittää /auth polun kirjautumista ja sen tarkistamista varten.
     * @param user Security principal, joka liittyy evästeesen.
     *             Null, jos ei kirjautuneena.
     * @return Käyttäjän principal, jos kirjautunut, muuten null.
     */
    @RequestMapping("/auth")
    @ResponseBody
    public Principal user(Principal user) {
        return user;
    }

}
