package sotechat.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * WebSocket-viestin testaamista helpottava luokka. Loytyy metodi Json-
 * muotoisen viestin generoimiseksi, seka apumetodit sen tarkistamiseksi,
 * etta vastausviestissa on vain halutut kentat.
 */
public class MsgUtil {
    /**
     * Settiin lisataan ne kentat joiden halutaan loytyvan palvelimen
     * lahettamasta vastauksesta. Eli ne kentat, jotka loytyvat MsgToClient-
     * oliosta. Settiin ei haluta lisata sellaisia kenttia, jotka loytyvat
     * palvelimelle lahetettavasta viestista(MsgToServer), mutta jotka
     * eivat loydy palvelimen lahettamasta vastauksesta(MsgToClient).
     */
    private HashSet<String> halutaanLahettaa;
    /**
     * Mappiin talletetaan kaikki avaimet ja niita vastaavat arvot. Seka ne
     * avain-arvo -parit, jotka loytyvat MsgToClient-olioista, etta ne, jotka
     * loytyvat MsgToServer-olioista.
     */
    private HashMap<String, String> map;

    public MsgUtil() {
        this.halutaanLahettaa = new HashSet<>();
        this.map = new HashMap<>();
    }

    /**
     * Tassa metodissa maaritellaan, minka kenttien halutaan loytyvan
     * vastausviestista. Kun lisataan avain-arvo -pareja mappiin, maaritellaan
     * lisaksi boolean-muuttujalle arvo. Se on true, jos halutaan, etta avain
     * loytyy palvelimen lahettamasta vastausviestista. False silloin, jos
     * avaimen ei haluta loytyvan. Settiin lisataan vain ne arvot, joiden
     * halutaan loytyvan vastauksesta, kun taas Mappiin lisataan kaikki.
     *
     * @param key Avain, esim userId tai channelId
     * @param value Arvo, esim userId:n arvo voi olla 542
     * @param lahetetaan Halutaanko, etta palvelimen vastausviestista
     *                   loytyy tietty kentta. Esim. ei haluta, etta
     *                   palvelimen paluuviestissa olisi userId.
     */
    public void add(String key, String value, boolean lahetetaan) {
        if (lahetetaan) halutaanLahettaa.add(key);
        map.put(key, value);
    }

    /**
     * Luodaan Mappiin talletetuista avain-arvo -pareista Json-muotoinen String,
     * joka voidaan lahettaa palvelimelle.
     * @return Palauttaa Json-muotoisen Stringin
     */
    public String mapToString() {
        String jsonString = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonString += "\"" + entry.getKey() + "\"" + ":" + "\"" + entry
                    .getValue() + "\"" + ",";
        }
        jsonString = "{" + jsonString.substring(0, jsonString.length()-1) + "}";
        return jsonString;
    }

    /**
     * Palauttaa Setin, jota voidaan kayttaa testaamisessa apuvalineena.
     * Setissa on ne arvot joiden halutaan loytyvan vastausviestista.
     * @return
     */
    public HashSet<String> getMsgUtilSet() {
        return halutaanLahettaa;
    }
}
