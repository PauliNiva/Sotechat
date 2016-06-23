package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class CategoryController {

    @Autowired
    SessionRepo sessionRepo;


    /** Kun asiakas tulee paasivulle tulokategorian nayttavan linkin kautta.
     * esim. www.sotechat.com/from/mielenterveys
     * @param category polusta haettu kategoriamuuttuja
     * @param req pyynto
     * @param professional autentikaatiotiedot
     * @return ohjataan kayttaja juureen
     */
    @RequestMapping(value = "/from/{category}", method = RequestMethod.GET)
    public final String rememberCategoryAndForward(
            final @PathVariable String category,
            final HttpServletRequest req,
            final Principal professional
    ) {
        Session session = sessionRepo.updateSession(req, professional);
        session.set("category", category);
        /** Ohjataan juureen, eli tavallisen kayttajan sivulle. */
        return "redirect:/";
    }
}
