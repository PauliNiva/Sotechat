package sotechat.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AngularTestController {

    @RequestMapping(value = "/pro")
    public String pro() {
        return "forward:/proCP.html";
    }

    @RequestMapping(value ={"/chat", "/inQueue"})
    public String redirect() {
        return "forward:/";
    }




}
