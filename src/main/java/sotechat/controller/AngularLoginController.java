package sotechat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class AngularLoginController {

    @RequestMapping("/auth")
    @ResponseBody
    public String user(Principal user) {
        if (user != null) {
            return "{\"name\":\"" + user.getName() + "\"}";
        }
        return "{}";
    }

}
