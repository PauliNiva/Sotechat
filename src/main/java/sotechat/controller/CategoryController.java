package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@Controller
public class CategoryController {

    @Autowired
    SessionRepo sessionRepo;


    /** Kun asiakas tulee paasivulle tulokategorian nayttavan linkin kautta.
     * esim. www.sotechat.com/from/source?=mielenterveys
     * @param source polusta haettu kategoriamuuttuja
     * @param req pyynto
     * @param professional autentikaatiotiedot
     * @return ohjataan kayttaja juureen
     */
    @RequestMapping(value = "/from/", method = RequestMethod.GET)
    public final String rememberCategoryAndForward(
            final @RequestParam String source,
            final HttpServletRequest req,
            final Principal professional
    ) {
        Session session = sessionRepo.updateSession(req, professional);
        session.set("category", source);
        /** Pyydetaan clientia tekemaan uudelleenohjaus juureen. */
        return "redirect:/";
    }
}
