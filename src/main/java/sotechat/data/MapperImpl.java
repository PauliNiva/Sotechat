package sotechat.data;

import org.springframework.stereotype.Component;
import sotechat.service.DatabaseService;
import sotechat.wrappers.MsgToClient;

import java.util.*;


/**
 * Mapper muistaa asioita kanaviin ja ID:hen liittyen. Esimerkiksi:
 * - Onko jokin username varattu rekisteroityneelle kayttajalle?
 * - Getteri Channel-olioille parametrilla channelId
 */
@Component
public class MapperImpl implements Mapper {

    /**
     * Kanava-mappi. Avain channelId, arvo Channel-olio.
     */
    private Map<String, Channel> channels;

    /**
     * Satunnaismerkkijonogeneraattori.
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
     * Tietokantapalvelut.
     */
    private DatabaseService databaseService;


    /**
     * Konstruktori.
     */
    public MapperImpl() {
        this.channels = new HashMap<>();
        this.fastGen = new FastGeneratorForRandomStrings();
        this.reservedIds = new HashSet<>();
        this.mapRegisteredUsers = new HashMap<>();
    }

    /**
     * Palauttaa <code>Channel</code>-olion.
     *
     * @param channelId channelId.
     * @return <code>Channel</code>-olio tai <code>null</code> jos ei loydy.
     */
    @Override
    public synchronized Channel getChannel(
            final String channelId
    ) {
        Channel channel = channels.get(channelId);
        if (channel == null) {
            /* Kanavaa ei loydy muistista, luodaan se. */
            channel = new Channel(channelId);
            channels.put(channelId, channel);
            /* Haetaan kanavan lokit tietokannasta (tai tyhja lista). */

            List<MsgToClient> logs = databaseService.retrieveMessages(channelId);
            for (MsgToClient msg : logs) {
                String username = msg.getUsername();
                if (isUsernameReserved(username)) {
                    /* Loydettiin ammattilaisen kirjoittama viesti, joten
                     * lisataan kirjoittaja historiallisiin kayttajiin. */
                    String userId = getIdFromRegisteredName(username);
                    channel.addHistoricUserId(userId);
                }
            }
        }
        return channel;
    }

    /**
     * Luo uuden kanava-olion, kirjaa sen muistiin ja palauttaa sen.
     *
     * @return luotu kanava-olio.
     */
    @Override
    public synchronized Channel createChannel() {
        String channelId = generateNewId();
        Channel channel = new Channel(channelId);
        channels.put(channelId, channel);
        return channel;
    }

    /**
     * Poistaa kanavan, joka vastaa parametrina annettua channelId:ta.
     * Jos palvelin on pitkaan paalla, halutaan vapauttaa vanhojen
     * kanavien tietoja muistista.
     * @param channelId channelId
     */
    @Override
    public synchronized void forgetChannel(final String channelId) {
        channels.remove(channelId);
    }

    /**
     * Liittaa ammattilaisen kayttajanimen ja kayttajatunnuksen toisiinsa.
     *
     * @param username p.
     * @param userId p.
     */
    @Override
    public synchronized void mapProUsernameToUserId(
            final String username,
            final String userId
    ) {
        this.mapRegisteredUsers.put(username, userId);
    }

    @Override
    public synchronized void reserveId(
            final String someId
    ) {
        this.reservedIds.add(someId);
    }

    /**
     * Unohtaa, että jokin username oli varattu. Käyttäjän poiston yhteydessä.
     *
     * @param username <code>username</code>.
     */
    @Override
    public synchronized void removeMappingForUsername(
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
    @Override
    public synchronized boolean isUsernameReserved(
            final String username
    ) {
        return mapRegisteredUsers.containsKey(username);
    }

    /**
     * Hakee salaisen kayttajaID:n. argumenttina julkinen kayttajanimi.
     * Kaytetaan vain rekisteroityjen kayttajien tapauksessa.
     *
     * @param username Julkinen <code>username</code>.
     * @return id Salainen id.
     */
    @Override
    public synchronized String getIdFromRegisteredName(
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
    @Override
    public synchronized String generateNewId() {
        String newId;
        do {
            newId = getFastRandomString();
        } while (reservedIds.contains(newId));
        reserveId(newId);
        return newId;
    }

    /**
     * Nopea satunnaismerkkijonotuottaja (kaytossa).
     *
     *  @return Satunnaismerkkijono.
     */
    @Override
    public String getFastRandomString() {
        return fastGen.nextString();
    }

    @Override
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
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
