package sotechat.util;

import sotechat.data.Mapper;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * Yleishyodyllisia staattisia funktioita.
 */
public final class Utils {

    /** Esim: get(session, "username") -> "Matti".
     * Toistoa oli niin paljon, että eriytettiin omaksi metodiksi.
     * Palauttaa nullin sijaan tyhjän Stringin, jotta käsittely helpottuisi.
     * @param session HttpSession-objekti
     * @param attributeName Avain haettavalle attribuutille
     * @return Haettavan attribuutin arvo Stringinä
     */
    public static String get(
            final HttpSession session,
            final String attributeName
    ) {
        if (session == null) {
            return "";
        }
        Object value = session.getAttribute(attributeName);
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    /**
     * This exists to solve a CheckStyle warning.
     */
    private Utils() {
        /** Not called. */
    }
}
