package sotechat.data;

import java.util.List;
import java.util.Set;

/**
 * Jotta Springin Dependency Injection toimisi,
 * Mapperilla taytyy olla oma interface.
 */
public interface Mapper {
    /** Metodi, jonka tarkoituksena on tallettaa tietorakenteeseen
     * avain-arvo pari, jossa kayttajan ID on avain ja kayttajanimi on arvo,
     * johon avain viittaa.
     * @param id Kayttajan ID.
     * @param username Kayttajatunnus
     */
    void mapUsernameToId(String id, String username);

    /** Metodi, joka mahdollistaa kayttajanimen hakemisen tietorakenteesta
     * kayttajan id:n perusteella.
     * @param id Kayttajan ID.
     * @return Palauttaa kayttajanimen.
     */
    String getUsernameFromId(String id);

    /** Metodi joka mahdollistaa rekisteroityneen kayttajan(hoitajan)
     * kayttajaId:n hakemisen tietorakenteesta kayttajanimen perusteella.
     * Hoitajan kayttajanimi on uniikki, koska rekisteroityminen vaatii sita.
     * @param registeredName Rekisteroityneen kayttajan kayttajanimi.
     * @return Palauttaa rekisteroityneen kayttajan ID:n.
     */
    String getIdFromRegisteredName(String registeredName);

    /** Metodi generoi uniikin id:n kayttajalle.
     * @return Palauttaa generoidun ID:n.
     */
    String generateNewId();

    /** Metodi tarkistaa, onko tietty kayttajaId jo talletettuna
     * tietorakenteeseen.
     * @param id Se kayttajan ID, jonka olemassaolo tietorakenteesta halutaan
     *           tarkistaa.
     * @return Palauttaa true, jos kayttaja ID loytyy, ja false, jos ei loydy.
     */
    boolean isUserIdMapped(String id);

    /** Metodi tarkistaa, kuuluuko jokin kayttajaID
     * rekisteroityneelle kayttajalle.
     * @param id id
     * @return true, jos kuuluu.
     */
    boolean isUserProfessional(String id);

    Set<Session> getSubscribers(
            final String channelId
    );

    void addSessionToChannel(
            String channelIdWithPath,
            Session session
    );
    void removeSessionToChannel(
            final String channelIdWithPath,
            final Session session
    );
}
