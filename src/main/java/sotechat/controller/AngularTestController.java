package sotechat.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/** Forward for /pro.
 */
@Controller
public class AngularTestController {

    /** Forward /pro to /proCp.html
     * @return proCP.html
     */
    @RequestMapping(value = "/pro")
    public final String pro() {
        return "forward:/proCP.html";
    }

}
