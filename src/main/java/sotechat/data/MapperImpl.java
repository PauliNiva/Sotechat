package sotechat.data;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;
import java.security.SecureRandom;
import java.math.BigInteger;

/** Mapperi muistaa, miten nimimerkit ja ID:t ovat yhteydessä.
 * HUOM: ID on yksilöllinen, nimimerkki ei (muuta kuin
 * rekisteröityneillä käyttäjillä). Esim. voi olla useita "Anon".
 */
@Component
public class MapperImpl implements Mapper {

    /** map key=id, value=username, esim. map.get("491829813") => "Anon". */
    private HashMap<String, String> map;
    /** revMap key=username, value=id, esim. revMap.get("hoitaja anne") => "annenId". */
    private HashMap<String, String> revMap;
    /** Raskas satunnaislukugeneraattori. */
    private SecureRandom random;
    /** Nopea satunnaismerkkijonogeneraattori (joka käyttää normi Randomia). */
    private FastGeneratorForRandomStrings fastGen;

    /** Konstruktori alustaa singleton-instanssin Mapperista. */
    public MapperImpl() {
        this.map = new HashMap<String, String>();
        this.revMap = new HashMap<String, String>();
        this.random = new SecureRandom();
        this.fastGen = new FastGeneratorForRandomStrings(16);

        /** Kovakoodataan yksi hoitaja devausvaiheessa. */
        mapUsernameToId("666", "hoitaja");
    }

    /** Tallentaa molempiin suuntiin tiedon id<->username
     * @param id salainen id
     * @param username julkinen username
     */
    @Override
    public void mapUsernameToId(String id, String username) {
        this.map.put(id, username);
        this.revMap.put(username, id);
    }

    /** Getteri julkiselle käyttäjänimelle,
     * parametrina salainen käyttäjäID.
     * @param id id
     * @return username
     */
    @Override
    public String getUsernameFromId(String id) {
        if (!map.containsKey(id)) {
            return "UNKNOWN_USERNAME";
        }
        return this.map.get(id);
    }

    /** Kertoo, onko userId käytössä.
     * @param id userId
     * @return true/false
     */
    @Override
    public boolean isUserIdMapped(String id) {
        return (map.containsKey(id));
    }

    /** Getteri salaiselle käyttäjäID:lle,
     * parametrina julkinen käyttäjänimi.
     * Huom: mielekästä käyttää vain rekisteröityjen
     * käyttäjien tapauksessa. Jos kysytään vaikka
     * nimimerkin "Anon" ID:tä, on mielivaltaista,
     * mikä ID sieltä sattuu tulemaan.
     * @param registeredName julkinen username
     * @return id salainen id
     */
    @Override
    public String getIdFromRegisteredName(String registeredName) {
        /* Varmistetaan ensin, että username tunnetaan. */
        if (!revMap.containsKey(registeredName)) {
            /* Ei pitäisi laueta tuotannossa koskaan. */
            return "UNKNOWN_ID";
        }
        return this.revMap.get(registeredName);
    }

    /** Tuottaa uuden, yksilöllisen, salaisen userID:n.
     * ID:tä ei varata tai mäpätä vielä tässä kutsussa.
     * @return userId
     */
    @Override
    public String generateNewId() {
        while (true) {
            /** Tuotetaan satunnaismerkkijonoja niin kauan,
             * että vapaa merkkijono löytyy. On erittäin
             * epätodennäköistä, että iteraatioita olisi
             * koskaan enempää kuin yksi. */
            String userId = getFastRandomString();
            if (!map.containsKey(userId)) {
                return userId;
            }
        }
    }

    /** Ei käytössä. Jos halutaan siirtyä "satunnaisempaan",
     * mutta hitaampaan generaattoriin. Tässä tapauksessa
     * olisi hyvä varmistaa, ettei palvelinta voi kyykyttää
     * pyynnöillä, jotka aiheuttavat tämän metodin jatkuvaa
     * kutsumista.
     * @return satunnaismerkkijono
     */
    public String getSecureRandomString() {
        return "" + new BigInteger(130, random).toString(32);
    }

    /** Nopea satunnaismerkkijonotuottaja (käytössä).
     * @return satunnaismerkkijono
     */
    public String getFastRandomString() {
        return fastGen.nextString();
    }

    /** Nopea pseudosatunnaismerkkijonotuottaja.
     * Attribution: http://stackoverflow.com/questions/
     * 41107/how-to-generate-a-random-alpha-numeric-string
     */
    private class FastGeneratorForRandomStrings {

        /** Käytetään nopeaa randomia. */
        private final Random random = new Random();
        /** Sisältää aakkoston, jonka merkkejä satunnaisjonot voivat sisältää. */
        private final char[] symbols;
        /** Tilapäistaulukko uuden merkkijonon muodostukseen. */
        private final char[] buf;

        /** Konstruktori alustaa olion (yksi olio riittää).
         * @param length pituus halutuille merkkijonoille
         */
        public FastGeneratorForRandomStrings(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();
        }

        /** Palauttaa satunnaismerkkijonon.
         * @return satunnaismerkkijono
         */
        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }
}
