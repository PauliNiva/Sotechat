package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Message;

/**
 * JPA-repositorio, joka toimii sovelluksen ja tietokannan valisena rajapintana.
 * Mahdollistaa Message-muotoisten Java-olioiden tallentamisen tietokantaan, ja
 * vastaavasti tietokannan Message-taulusta rivien hakemisen ja tallentamisen
 * Message-olioiksi.
 */
public interface MessageRepo extends JpaRepository<Message, Long> {
}
