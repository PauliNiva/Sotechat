package sotechat.data;

import sotechat.service.DatabaseService;

/**
 * Mapper-rajapinta kanaviin ja ID:hen liittyvien asioiden muistamiseen.
 */
public interface Mapper {

    /**
     * Palauttaa <code>Channel</code>-olion.
     *
     * @param channelId channelId.
     * @return <code>Channel</code>-olio tai <code>null</code> jos ei loydy.
     */
    Channel getChannel(final String channelId);


    /**
     * Luo uuden kanava-olion, kirjaa sen muistiin ja palauttaa sen.
     *
     * @return luotu kanava-olio.
     */
    Channel createChannel();

    /**
     * Poistaa kanavan, joka vastaa parametrina annettua channelId:ta.
     * Jos palvelin on pitkaan paalla, halutaan vapauttaa vanhojen
     * kanavien tietoja muistista.
     *
     * @param channelId channelId
     */
    void forgetChannel(final String channelId);

    /**
     * Liittaa ammattilaisen kayttajanimen ja kayttajatunnuksen toisiinsa.
     *
     * @param username p.
     * @param userId p.
     */
    void mapProUsernameToUserId(final String username, final String userId);

    /**
     * Varaa Id:n.
     *
     * @param someId Varattava Id.
     */
    void reserveId(final String someId);

    /**
     * Unohtaa, että jokin username oli varattu. Käyttäjän poiston yhteydessä.
     *
     * @param username <code>username</code>.
     */
    void removeMappingForUsername(final String username);

    /**
     * Tarkistaa onko kayttajanimi varattu.
     *
     * @param username kayttajanimi.
     * @return <code>true</code> jos kayttajanimi on varattu.
     * <code>false</code> muulloin.
     */
    boolean isUsernameReserved(final String username);

    /**
     * Hakee salaisen kayttajaID:n. argumenttina julkinen kayttajanimi.
     * Kaytetaan vain rekisteroityjen kayttajien tapauksessa.
     *
     * @param username Julkinen <code>username</code>.
     * @return id Salainen id.
     */
    String getIdFromRegisteredName(final String username);

    /**
     * Tuottaa ja varaa uuden yksilollisen ID:n (userId/channelId).
     *
     * @return userId.
     */
    String generateNewId();

    /**
     * Nopea satunnaismerkkijonotuottaja (kaytossa).
     *
     *  @return Satunnaismerkkijono.
     */
    String getFastRandomString();

    /**
     * Asettaa tietokantapalvelun.
     *
     * @param pDatabaseService Asetettava tietokantapalvelu.
     */
    void setDatabaseService(final DatabaseService pDatabaseService);
}
