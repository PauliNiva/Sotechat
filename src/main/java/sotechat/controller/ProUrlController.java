package sotechat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Uudelleenohjaus polkuun "/pro".
 */
@Controller
public class ProUrlController {

    /**
     * Uudelleenohjaus polun "/pro" polkuun "/proCp.html".
     * @return proCP.html
     */
    @RequestMapping(value = "/pro")
    public final String pro() {
        return "forward:/proCP.html";
    }

    /**
     * Uudelleenohjaa polun "/login" polkuun "/pro".
     * @return /pro
     */
    @RequestMapping(value = "/login")
    public final String login() {
        return "redirect:/pro";
    }

}
