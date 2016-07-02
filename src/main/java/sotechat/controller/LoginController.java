package sotechat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Client tarkastaa onko kayttaja kirjautunut, seka
 * lahettaa kirjautumistiedot <code>HTTPBasicAuthentication</code>:in mukaan.
 */
@RestController
public class LoginController {

    /**
     * Vastaa kayttajalle JSON-muodossa kirjautumistiedot tai tyhjalla
     * viestilla, jos kayttaja ei ole kirjautunut.
     *
     * @param user Security Principal.
     * @return Käyttäjän principal JSON-muodossa, jos kirjautunut,
     * muuten tyhja vastaus.
     */
    @RequestMapping("/auth")
    @ResponseBody
    public Principal user(final Principal user) {
        return user;
    }

}
