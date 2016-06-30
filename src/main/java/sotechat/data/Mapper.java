package sotechat.data;

import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Random;

/**
 * Mapperi muistaa asioita kanaviin ja ID:hen liittyen. Esim:
 * - Onko jokin username varattu rekisteroityneelle kayttajalle?
 * - Getteri Channel-olioille parametrilla channelId
 */
@Component
public class Mapper {

    /**
     * Kanava-mappi. Avain channelId, arvo Channel-olio.
     */
    private Map<String, Channel> channels;

    /**
     * Nopea satunnaismerkkijonogeneraattori (joka kayttaa normi Randomia).
     */
    private FastGeneratorForRandomStrings fastGen;

    /**
     * Muistetaan, mitka generaattorin tuottamat ID:t on jo varattu.
     */
    private Set<String> reservedIds;

    /**
     * Rekisteroityneet kayttajat. Avain username, arvo userId.
     */
    private Map<String, String> mapRegisteredUsers;


    /**
     * Konstruktori alustaa singleton-instanssin Mapperista.
     */
    public Mapper() {
        this.channels = new HashMap<>();
        this.fastGen = new FastGeneratorForRandomStrings();
        this.reservedIds = new HashSet<>();
        this.mapRegisteredUsers = new HashMap<>();
        /* TODO Lataa tietokannasta reservedIds & usernames. */
    }

    /**
     * Palauttaa channel-olion. Yrittaa ensin muistista, sitten db.
     *
     * @param channelId channelId.
     * @return <code>channel</code>-olio tai <code>null</code> jos ei loydy.
     */
    public Channel getChannel(
            final String channelId
    ) {
        Channel channel = channels.get(channelId);
        if (channel == null) {
            //TODO Try loading from db.
        }
        return channel;
    }

    /**
     * Luo uuden kanava-olion, kirjaa sen muistiin ja palauttaa sen.
     *
     * @return luotu kanava-olio.
     */
    public Channel createChannel() {
        String channelId = generateNewId();
        Channel channel = new Channel(channelId);
        channels.put(channelId, channel);
        return channel;
    }

    /**
     * Liittaa ammattilaisen kayttajanimen ja kayttajatunnuksen toisiinsa.
     *
     * @param username p.
     * @param userId p.
     */
    public final void mapProUsernameToUserId(
            final String username,
            final String userId
    ) {
        this.mapRegisteredUsers.put(username, userId);
    }

    /**
     * Unohtaa, että jokin username oli varattu. Käyttäjän poiston yhteydessä.
     *
     * @param username <code>username</code>.
     */
    public final void removeMappingForUsername(
            final String username
    ) {
        this.mapRegisteredUsers.remove(username);
    }

    /**
     * Tarkistaa onko kayttajanimi varattu.
     *
     * @param username kayttajanimi.
     * @return <code>true</code> jos kayttajanimi on varattu.
     * <code>false</code> muulloin.
     */
    public final synchronized boolean isUsernameReserved(
            final String username) {
        return mapRegisteredUsers.containsKey(username);
    }

    /**
     * Hakee salaisen kayttajaID:n. argumenttina julkinen kayttajanimi.
     * Kaytetaan vain rekisteroityjen kayttajien tapauksessa.
     *
     * @param username Julkinen <code>username</code>.
     * @return id Salainen id.
     */
    public final synchronized String getIdFromRegisteredName(
            final String username
    ) {
        /* Varmistetaan ensin, etta username tunnetaan. */
        if (username == null
                || username.isEmpty()
                || !mapRegisteredUsers.containsKey(username)) {
            throw new IllegalArgumentException(("Error! Unknown userId for "
                    + "registered name " + username));
        }
        return this.mapRegisteredUsers.get(username);
    }

    /**
     * Tuottaa ja varaa uuden yksilollisen ID:n (userId/channelId).
     *
     * @return userId.
     */
    public final synchronized String generateNewId() {
        String newId;
        do {
            newId = getFastRandomString();
        } while (reservedIds.contains(newId));
        reservedIds.add(newId);
        return newId;
    }

    /**
     * Nopea satunnaismerkkijonotuottaja (kaytossa).
     *
     *  @return Satunnaismerkkijono.
     */
    public final String getFastRandomString() {
        return fastGen.nextString();
    }

    /**
     * Nopea pseudosatunnaismerkkijonotuottaja.
     * Attribution: http://stackoverflow.com/questions/
     * 41107/how-to-generate-a-random-alpha-numeric-string.
     */
    private class FastGeneratorForRandomStrings {

        /* Kaytetaan nopeaa randomia. */
        private final Random random = new Random();
        /* Haluttu pituus satunnaismerkkijonoille. */
        private static final int LENGTH = 16;

        /* Sisaltaa aakkoston, jonka merkkeja satunnaisjonot voi sisaltaa. */
        private final char[] symbols;
        /* Tilapaistaulukko uuden merkkijonon muodostukseen. */
        private final char[] buf;

        /* Konstruktori alustaa olion (yksi olio riittaa). */
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

        /**
         * Palauttaa satunnaismerkkijonon.
         *
         * @return Satunnaismerkkijono.
         */
        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx) {
                buf[idx] = symbols[random.nextInt(symbols.length)];
            }
            return new String(buf);
        }
    }

}
