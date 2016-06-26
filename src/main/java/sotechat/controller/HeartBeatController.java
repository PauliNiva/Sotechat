package sotechat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tama luokka on olemassa vain clientin konsolilogin siistimiseksi.
 * Client lahettaa "Heartbeatin" HTTP POST:lla saannollisin valiajoin,
 * jotta sessio ei kuolisi. Muutoin esim. 4 tunnin WebSocket-chatti
 * saattaisi loppua siihen, etta kaikki liikenne tapahtuu WebSocketin
 * sisalla ja HTTP-sessio kuolee.
 *
 * HeartBeat toimisi myos ilman palvelimen vastauksia, mutta silloin
 * clientin konsolilogissa nakyisi 404 ilmoituksia. Talla kontrollerilla
 * saadaan siistittya 404 ilmoitukset pois.
 */
@RestController
public class HeartBeatController {


    /** Client ei tee mitaan talla vastauksella, joka lahetetaan.
     * @return vastataan konsolilokin siistimiseksi {"heartbeat":"server-alive"}
     */
    @RequestMapping(value = "/toServer/heartBeat", method = RequestMethod.POST)
    public final String returnHeartBeat() {
        return "{\"heartbeat\":\"server-alive\"}";
    }

}
