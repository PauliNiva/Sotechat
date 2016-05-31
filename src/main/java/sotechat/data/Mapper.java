package sotechat.data;

/**
 * Jotta Springin Dependency Injection toimisi,
 * Mapperilla täytyy olla oma interface.
 */
public interface Mapper {
    /** Metodi, jonka tarkoituksena on tallettaa tietorakenteeseen
     * avain-arvo pari, jossa käyttäjän ID on avain ja käyttäjänimi on arvo,
     * johon avain viittaa.
     * @param id Käyttäjän ID.
     * @param username Käyttäjätunnus
     */
    void mapUsernameToId(String id, String username);

    /** Metodi, joka mahdollistaa käyttäjänimen hakemisen tietorakenteesta
     * käyttäjän id:n perusteella.
     * @param id Käyttäjän ID.
     * @return Palauttaa käyttäjänimen.
     */
    String getUsernameFromId(String id);

    /** Metodi joka mahdollistaa rekisteröityneen käyttäjän(hoitajan)
     * käyttäjäId:n hakemisen tietorakenteesta käyttäjänimen perusteella.
     * Hoitajan käyttäjänimi on uniikki, koska rekisteröityminen vaatii sitä.
     * @param registeredName Rekisteröityneen käyttäjän käyttäjänimi.
     * @return Palauttaa rekisteröityneen käyttäjän ID:n.
     */
    String getIdFromRegisteredName(String registeredName);

    /** Metodi generoi uniikin id:n käyttäjälle.
     * @return Palauttaa generoidun ID:n.
     */
    String generateNewId();

    /** Metodi tarkistaa, onko tietty käyttäjäId jo talletettuna
     * tietorakenteeseen.
     * @param id Se käyttäjän ID, jonka olemassaolo tietorakenteesta halutaan
     *           tarkistaa.
     * @return Palauttaa true, jos käyttäjä ID löytyy, ja false, jos ei löydy.
     */
    boolean isUserIdMapped(String id);
    boolean isUserProfessional(String id);
}
