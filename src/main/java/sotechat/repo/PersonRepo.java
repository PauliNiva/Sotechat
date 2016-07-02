package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Person;

/**
 * <code>JPA</code>-sailo, joka toimii sovelluksen ja tietokannan valisena
 * rajapintana. Sailo mahdollistaa <code>Person</code>-olioiden
 * tallentamisen tietokantaan ja vastaavasti rivien hakemisen tietokannan
 * <code>Person</code>-taulusta ja naiden tallentamisen
 * <code>Person</code>-olioiksi.
 */
public interface PersonRepo extends JpaRepository<Person, String> {

    /**
     * Hakee <code>loginName</code>:n perusteella <code>Person</code>-olion.
     *
     * @param loginName Kirjautumistunnus.
     * @return <code>JPA</code>-sailosta loydetty <code>Person</code>-olio.
     */
    Person findByLoginName(String loginName);
}
