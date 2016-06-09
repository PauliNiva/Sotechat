package sotechat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP Heartbeat HTTP Session yllapitamista varten:
 * Client lahettaa aina valilla jotain HTTP POST:lla, jotta Sessio elaisi.
 * Tama luokka on olemassa, jotta clientin konsolilogissa ei nakyisi 404.
 * TODO: WS Heartbeat jotta client voi huomata kun yhteys katkeaa.
 */
@RestController
public class HeartBeatController {


    /** Spring magically keeps the HTTP Session alive as long as
     * some HTTP action is going on.
     * We don't really do anything with incoming/outgoing values.
     * @return just to clear 404 from console logs, {"heartbeat":"server-alive"}
     */
    @RequestMapping(value = "/toServer/heartBeat", method = RequestMethod.POST)
    public final String returnHeartBeat() {
        return "{\"heartbeat\":\"server-alive\"}";
    }

}
