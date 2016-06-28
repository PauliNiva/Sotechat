package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Message;

/**
 * <code>JPA</code>-sailo, joka toimii sovelluksen ja tietokannan valisena
 * rajapintana. Sailo mahdollistaa <code>Message</code>-olioiden
 * tallentamisen tietokantaan ja vastaavasti rivien hakemisen tietokannan
 * <code>Message</code>-taulusta ja naiden tallentamisen
 * <code>Message</code>-olioiksi.
 */
public interface MessageRepo extends JpaRepository<Message, Long> {

}
