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


    /** esim. mapByUserId.get("4dfsior13") => "Anon". */
    private HashMap<String, String> mapByUserId;
    /** esim. mapByUsername.get("hoitaja anne") => "inaxsiu9eisdf". */
    private HashMap<String, String> mapByUsername;
    /** professionalIDs:lta voi kysya, mitka ID:t ovat rekisteroity. */
    private HashSet<String> professionalIDs;


    /** Raskas satunnaislukugeneraattori. */
    private SecureRandom random;
    /** Nopea satunnaismerkkijonogeneraattori (joka kayttaa normi Randomia). */
    private FastGeneratorForRandomStrings fastGen;

    /** Konstruktori alustaa singleton-instanssin Mapperista. */
    public MapperImpl() {
        this.mapByUserId = new HashMap<String, String>();
        this.mapByUsername = new HashMap<String, String>();
        this.random = new SecureRandom();
        this.fastGen = new FastGeneratorForRandomStrings();
        this.professionalIDs = new HashSet<>();

        /** Kovakoodataan yksi hoitaja devausvaiheessa. */
        mapUsernameToId("666", "hoitaja");
        professionalIDs.add("666");
        mapUsernameToId("667", "Hoitaja2");
        professionalIDs.add("667");
    }

    /** Tallentaa sekä tiedon mikä id vastaa mitäkin usernamea että mikä
     * username vastaa mitäkin id:tä
     *
     * @param id salainen id
     * @param username julkinen username
     */
    @Override
    public final synchronized void mapUsernameToId(
            final String id,
            final String username
    ) {
        this.mapByUserId.put(id, username);
        this.mapByUsername.put(username, id);
    }

    /** Getteri julkiselle kayttajanimelle,
     * parametrina salainen kayttajaID.
     * @param id id
     * @return username
     */
    @Override
    public final synchronized String getUsernameFromId(
            final String id
    ) {
        if (!mapByUserId.containsKey(id)) {
            return "UNKNOWN_USERNAME";
        }
        return this.mapByUserId.get(id);
    }

    /** Kertoo, onko userId kaytossa.
     * @param id userId
     * @return true/false
     */
    @Override
    public final synchronized boolean isUserIdMapped(
            final String id
    ) {
        return (mapByUserId.containsKey(id));
    }

    @Override
    public final synchronized boolean isUserProfessional(
            final String id
    ) {
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
    public final synchronized String getIdFromRegisteredName(
            final String registeredName
    ) {
        /* Varmistetaan ensin, etta username tunnetaan. */
        if (registeredName == null
                || registeredName.isEmpty()
                || !mapByUsername.containsKey(registeredName)) {
            return "UNKNOWN_ID";
        }
        return this.mapByUsername.get(registeredName);
    }

    /** Tuottaa uuden, yksilollisen, salaisen userID:n.
     * ID:ta ei varata tai mapata viela tassa kutsussa.
     * @return userId
     */
    @Override
    public final synchronized String generateNewId() {
        while (true) {
            /** Tuotetaan satunnaismerkkijonoja niin kauan,
             * etta vapaa merkkijono loytyy. On erittain
             * epatodennakoista, etta iteraatioita olisi
             * koskaan enempaa kuin yksi. */
            String userId = getFastRandomString();
            if (!mapByUserId.containsKey(userId)) {
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
