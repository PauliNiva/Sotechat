package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Person;

/**
 * JPA-repositorio, joka toimii sovelluksen ja tietokannan valisena rajapintana.
 * Mahdollistaa Person-muotoisten Java-olioiden tallentamisen tietokantaan, ja
 * vastaavasti tietokannan Person-taulusta rivien hakemisen ja tallentamisen
 * Person-olioiksi.
 */
public interface PersonRepo extends JpaRepository<Person, String> {
    /**
     * Erikseen maaritelty hakumetodi, jolla voidaan hakea JPA-repositoriosta
     * kayttajan kirjautumistunnuksen perusteella Person-olio.
     *
     * @param loginName Kayttajan kirjautumistunnus.
     * @return JPA-repositoriosta loydetty Person-olio.
     */
    Person findByLoginName(String loginName);
}

