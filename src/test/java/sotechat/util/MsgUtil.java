package sotechat.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * WebSocket-viestin testaamista helpottava luokka. Löytyy metodi Json-
 * muotoisen viestin generoimiseksi, sekä apumetodit sen tarkistamiseksi,
 * että vastausviestissä on vain halutut kentät.
 */
public class MsgUtil {
    /**
     * Settiin lisätään ne kentät joiden halutaan löytyvän palvelimen
     * lähettämästä vastauksesta. Eli ne kentät, jotka löytyvät MsgToClient-
     * oliosta. Settiin ei haluta lisätä sellaisia kenttiä, jotka löytyvät
     * palvelimelle lähetettävästä viestistä(MsgToServer), mutta jotka
     * eivät löydy palvelimen lähettämästä vastauksesta(MsgToClient).
     */
    private HashSet<String> halutaanLahettaa;
    /**
     * Mappiin talletetaan kaikki avaimet ja niitä vastaavat arvot. Sekä ne
     * avain-arvo -parit, jotka löytyvät MsgToClient-olioista, että ne, jotka
     * löytyvät MsgToServer-olioista.
     */
    private HashMap<String, String> map;

    public MsgUtil() {
        this.halutaanLahettaa = new HashSet<>();
        this.map = new HashMap<>();
    }

    /**
     * Tässä metodissa määritellään, minkä kenttien halutaan löytyvän
     * vastausviestistä. Kun lisätään avain-arvo -pareja mappiin, määritellään
     * lisäksi boolean-muuttujalle arvo. Se on true, jos halutaan, että avain
     * löytyy palvelimen lähettämästä vastausviestistä. False silloin, jos
     * avaimen ei haluta löytyvän. Settiin lisätään vain ne arvot, joiden
     * halutaan löytyvän vastauksesta, kun taas Mappiin lisätään kaikki.
     *
     * @param key Avain, esim userId tai channelId
     * @param value Arvo, esim userId:n arvo voi olla 542
     * @param lahetetaan Halutaanko, että palvelimen vastausviestistä
     *                   löytyy tietty kenttä. Esim. ei haluta, että
     *                   palvelimen paluuviestissä olisi userId.
     */
    public void add(String key, String value, boolean lahetetaan) {
        if (lahetetaan) halutaanLahettaa.add(key);
        map.put(key, value);
    }

    /**
     * Luodaan Mappiin talletetuista avain-arvo -pareista Json-muotoinen String,
     * joka voidaan lähettää palvelimelle.
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
     * Palauttaa Setin, jota voidaan käyttää testaamisessa apuvälineenä.
     * Setissä on ne arvot joiden halutaan löytyvän vastausviestistä.
     * @return
     */
    public HashSet<String> getMsgUtilSet() {
        return halutaanLahettaa;
    }
}
