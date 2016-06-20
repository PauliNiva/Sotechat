package sotechat.data;

import org.springframework.stereotype.Component;

import java.util.*;
import java.math.BigInteger;

/** Mapperi muistaa asioita kanaviin ja ID:hen liittyen. Esim:
 * - Onko jokin username varattu rekisteroityneelle kayttajalle?
 * - Getteri Channel-olioille parametrilla channelId
 */
@Component
public class Mapper {

    /** Kanava-mappi. Avain channelId, arvo Channel-olio. */
    private Map<String, Channel> channels;

    /** Nopea satunnaismerkkijonogeneraattori (joka kayttaa normi Randomia). */
    private FastGeneratorForRandomStrings fastGen;

    /** Muistetaan, mitka generaattorin tuottamat ID:t on jo varattu. */
    private Set<String> reservedIds;

    /** Rekisteroityneet kayttajat. Avain username, arvo userId. */
    private Map<String, String> mapRegisteredUsers;


    /** Konstruktori alustaa singleton-instanssin Mapperista. */
    public Mapper() {
        this.channels = new HashMap<>();
        this.fastGen = new FastGeneratorForRandomStrings();
        this.reservedIds = new HashSet<>();
        this.mapRegisteredUsers = new HashMap<>();
        //TODO: Lataa tietokannasta varatut.
    }

    /** Palauttaa channel-olion. Yrittaa ensin muistista, sitten db,
     * jos ei vielakaan loydy niin luo uuden kanavan.
     * @param channelId channelId
     * @return channel-olio
     */
    public Channel getChannel(
            String channelId
    ) {
        // TODO: Old channels from db?
        Channel channel = channels.get(channelId);
        if (channel == null) {
            channel = new Channel(channelId);
            channels.put(channelId, channel);
            //TODO: Put to db as well.
        }
        return channel;
    }

    /** Luo uuden kanava-olion, kirjaa sen muistiin ja palauttaa sen.
     * @return luotu kanava-olio
     */
    public Channel createChannel() {
        String channelId = generateNewId();
        Channel channel = new Channel(channelId);
        channels.put(channelId, channel);
        return channel;
    }

    /** Puts key userId, value username.
     *
     * @param username p
     * @param userId p
     */
    public final void mapProUsernameToUserId(
            final String username,
            final String userId
    ) {
        this.mapRegisteredUsers.put(username, userId);
    }

    /**
     * Returns true if username is reserved.
     * @param username username
     * @return true if username is reserved
     */
    public final synchronized boolean isUsernameReserved(
            final String username
    ) {
        return mapRegisteredUsers.containsKey(username);
    }

    /** Getteri salaiselle kayttajaID:lle,
     * parametrina julkinen kayttajanimi.
     * Huom: mielekasta kayttaa vain rekisteroityjen
     * kayttajien tapauksessa. Jos kysytaan vaikka
     * nimimerkin "Anon" ID:ta, on mielivaltaista,
     * mika ID sielta sattuu tulemaan.
     * @param username julkinen username
     * @return id salainen id
     */
    public final synchronized String getIdFromRegisteredName(
            final String username
    ) {
        /* Varmistetaan ensin, etta username tunnetaan. */
        if (username == null
                || username.isEmpty()
                || !mapRegisteredUsers.containsKey(username)) {
            System.out.println("Error! Unknown userId for " + username);
            return "UNKNOWN_ID";
        }
        return this.mapRegisteredUsers.get(username);
    }

    /** Tuottaa ja varaa uuden yksilollisen ID:n (userId/channelId).
     * @return userId
     */
    public final synchronized String generateNewId() {
        while (true) {
            /** Tuotetaan satunnaismerkkijonoja niin kauan,
             * etta vapaa merkkijono loytyy. On erittain
             * epatodennakoista, etta iteraatioita olisi
             * koskaan enempaa kuin yksi. */
            String newId = getFastRandomString();
            if (!reservedIds.contains(newId)) {
                reservedIds.add(newId);
                return newId;
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

}
