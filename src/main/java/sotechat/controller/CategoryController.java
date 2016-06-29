package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import sotechat.data.Session;
import sotechat.data.SessionRepo;

/**
 * Kontrolleri asiakkaan ohjaamiseksi oikeaan kategoriaan.
 */
@Controller
public class CategoryController {

    /**
     * Sessioiden kasittely.
     */
    private SessionRepo sessionRepo;

    /**
     * Konstruktori.
     *
     * @param pSessionRepo Alustettava sailo.
     */
    @Autowired
    public CategoryController(
            final SessionRepo pSessionRepo
    ) {
        this.sessionRepo = pSessionRepo;
    }


    /**
     * Asiakkaan saapuessa paasivulle tulokategorian nayttavan linkin kautta
     * liitetaan sessioon tieto mihin kategoriaan asiakas kuuluu.
     * Esimerkiksi www.sotechat.com/from/?source=mielenterveys osoitteeseen.
     *
     * @param source Polusta haettu kategoriamuuttuja.
     * @param req Pyynto.
     * @param professional Autentikaatiotiedot.
     * @return Uudelleenohjaus juureen.
     */
    @RequestMapping(value = "/from/", method = RequestMethod.GET)
    public final String rememberCategoryAndForward(
            final @RequestParam String source,
            final HttpServletRequest req,
            final Principal professional
    ) {
        Session session = sessionRepo.updateSession(req, professional);
        session.set("category", source);
        /* Pyydetaan clientia tekemaan uudelleenohjaus juureen. */
        return "redirect:/";
    }

}
