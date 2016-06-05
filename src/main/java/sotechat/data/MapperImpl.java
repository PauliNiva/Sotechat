package sotechat.data;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.security.SecureRandom;
import java.math.BigInteger;

/** Mapperi muistaa, miten nimimerkit ja ID:t ovat yhteydessa.
 * HUOM: ID on yksilollinen, nimimerkki ei (muuta kuin
 * rekisteroityneilla kayttajilla). Esim. voi olla useita "Anon".
 */
@Component
public class MapperImpl implements Mapper {

    /** map key=id, value=username, esim. map.get("491829813") => "Anon". */
    private HashMap<String, String> map;
    /** revMap key=username, value=id,
     * esim. revMap.get("hoitaja anne") => "annenId". */
    private HashMap<String, String> revMap;
    /** professionalIDs:lta voi kysya, mitka ID:t ovat rekisteroity. */
    private HashSet<String> professionalIDs;


    /** Raskas satunnaislukugeneraattori. */
    private SecureRandom random;
    /** Nopea satunnaismerkkijonogeneraattori (joka kayttaa normi Randomia). */
    private FastGeneratorForRandomStrings fastGen;

    /** Konstruktori alustaa singleton-instanssin Mapperista. */
    public MapperImpl() {
        this.map = new HashMap<String, String>();
        this.revMap = new HashMap<String, String>();
        this.random = new SecureRandom();
        this.fastGen = new FastGeneratorForRandomStrings();
        this.professionalIDs = new HashSet<>();

        /** Kovakoodataan yksi hoitaja devausvaiheessa. */
        mapUsernameToId("666", "Hoitaja");
        professionalIDs.add("666");
    }

    /** Tallentaa molempiin suuntiin tiedon id<->username
     * @param id salainen id
     * @param username julkinen username
     */
    @Override
    public final void mapUsernameToId(final String id, final String username) {
        this.map.put(id, username);
        this.revMap.put(username, id);
    }

    /** Getteri julkiselle kayttajanimelle,
     * parametrina salainen kayttajaID.
     * @param id id
     * @return username
     */
    @Override
    public final String getUsernameFromId(final String id) {
        if (!map.containsKey(id)) {
            return "UNKNOWN_USERNAME";
        }
        return this.map.get(id);
    }

    /** Kertoo, onko userId kaytossa.
     * @param id userId
     * @return true/false
     */
    @Override
    public final boolean isUserIdMapped(final String id) {
        return (map.containsKey(id));
    }

    @Override
    public final boolean isUserProfessional(final String id) {
        return professionalIDs.contains(id);
    }

    /** Getteri salaiselle kayttajaID:lle,
     * parametrina julkinen kayttajanimi.
     * Huom: mielekasta kayttaa vain rekisteroityjen
     * kayttajien tapauksessa. Jos kysytaan vaikka
     * nimimerkin "Anon" ID:ta, on mielivaltaista,
     * mika ID sielta sattuu tulemaan.
     * @param registeredName julkinen username
     * @return id salainen id
     */
    @Override
    public final String getIdFromRegisteredName(final String registeredName) {
        /* Varmistetaan ensin, etta username tunnetaan. */
        if (registeredName == null
                || registeredName.isEmpty()
                || !revMap.containsKey(registeredName)) {
            return "UNKNOWN_ID";
        }
        return this.revMap.get(registeredName);
    }

    /** Tuottaa uuden, yksilollisen, salaisen userID:n.
     * ID:ta ei varata tai mapata viela tassa kutsussa.
     * @return userId
     */
    @Override
    public final String generateNewId() {
        while (true) {
            /** Tuotetaan satunnaismerkkijonoja niin kauan,
             * etta vapaa merkkijono loytyy. On erittain
             * epatodennakoista, etta iteraatioita olisi
             * koskaan enempaa kuin yksi. */
            String userId = getFastRandomString();
            if (!map.containsKey(userId)) {
                return userId;
            }
        }
    }

    /** Nopea satunnaismerkkijonotuottaja (kaytossa).
     *  @return satunnaismerkkijono
     */
    public final String getFastRandomString() {
        return fastGen.nextString();
    }

    /** Nopea pseudosatunnaismerkkijonotuottaja.
     * Attribution: http://stackoverflow.com/questions/
     * 41107/how-to-generate-a-random-alpha-numeric-string
     */
    private class FastGeneratorForRandomStrings {

        /** Kaytetaan nopeaa randomia. */
        private final Random random = new Random();
        /** Haluttu pituus satunnaismerkkijonoille. */
        private static final int LENGTH = 16;

        /** Sisaltaa aakkoston, jonka merkkeja satunnaisjonot voi sisaltaa. */
        private final char[] symbols;
        /** Tilapaistaulukko uuden merkkijonon muodostukseen. */
        private final char[] buf;

        /** Konstruktori alustaa olion (yksi olio riittaa). */
        FastGeneratorForRandomStrings() {
            if (LENGTH < 1) {
                throw new IllegalArgumentException("length < 1: " + LENGTH);
            }
            buf = new char[LENGTH];
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch) {
                tmp.append(ch);
            }
            for (char ch = 'a'; ch <= 'z'; ++ch) {
                tmp.append(ch);
            }
            symbols = tmp.toString().toCharArray();
        }

        /** Palauttaa satunnaismerkkijonon.
         * @return satunnaismerkkijono
         */
        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx) {
                buf[idx] = symbols[random.nextInt(symbols.length)];
            }
            return new String(buf);
        }
    }




    /** Ei kaytossa. Jos halutaan siirtya "satunnaisempaan",
     * mutta hitaampaan generaattoriin. Tassa tapauksessa
     * olisi hyva varmistaa, ettei palvelinta voi kyykyttaa
     * pyynnoilla, jotka aiheuttavat taman metodin jatkuvaa
     * kutsumista.
     * @return satunnaismerkkijono
     */
    public final String getSecureRandomString() {
        return "" + new BigInteger(maxBitLength, random)
                .toString(numeralSystem);
    }

    /** Suurin mahdollinen maara bitteja mita salaisessa merkkijonossa voi
     * olla. Random arpoo, kuinka monta bittia salaiseen merkkijonoon lopulta
     * tulee.
     */
    private final int maxBitLength = 130;
    /** Muuttujan arvo kertoo mihin lukujarjestelmaan luotu BigInteger
     * muunnetaan, kun siita luodaan merkkijonoesitys. Oletuksena toString
     * luo merkkijonoesityksen 10-jarjestelmaan, esim. "10".toString()
     * palauttaisi arvon 10. Tassa tapauksessa luodun BigIntegerin
     * merkkijonoesitys on kuitenkin ilmaistu kantaluvussa 32.
     */
    private final int numeralSystem = 32;
}
