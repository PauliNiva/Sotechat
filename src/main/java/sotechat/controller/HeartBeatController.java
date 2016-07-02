package sotechat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Luokka clientin konsolilogin siistimiseksi.
 * Client lahettaa "<code>Heartbeat</code>in" HTTP POST:lla saannollisin
 * valiajoin, jotta sessio ei kuolisi. Muutoin esimerkiksi neljan tunnin
 * <code>WebSocket</code>-chatti saattaisi katketa siihen,
 * etta kaikki liikenne tapahtuu WebSocketin sisalla ja HTTP-sessio kuolee.
 *
 * <code>Heartbeat</code> toimii myos ilman palvelimen vastauksia, mutta
 * silloin clientin konsolilogissa nakyisi 404-ilmoituksia. Talla kontrollerilla
 * saadaan siistittya 404-ilmoitukset pois.
 */
@RestController
public class HeartBeatController {

    /**
     * Vastaa <code>Heartbeat</code>-pyyntoon.
     * @return Vastataan {"heartbeat":"server-alive"}.
     */
    @RequestMapping(value = "/toServer/heartBeat", method = RequestMethod.POST)
    public final String returnHeartBeat() {
        return "{\"heartbeat\":\"server-alive\"}";
    }

}
