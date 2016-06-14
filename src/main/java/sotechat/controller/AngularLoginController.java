package sotechat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class AngularLoginController {

    @RequestMapping("/auth")
    public Principal user(Principal user) {
        return user;
    }
}
