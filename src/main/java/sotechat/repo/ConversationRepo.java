package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Conversation;

/**
 * JPA-repositorio, joka toimii sovelluksen ja tietokannan valisena rajapintana.
 * Mahdollistaa Conversation-muotoisten Java-olioiden tallentamisen
 * tietokantaan, ja vastaavasti tietokannan Conversation-taulusta rivien
 * hakemisen ja tallentamisen Conversation-olioiksi.
 */
public interface ConversationRepo extends JpaRepository<Conversation, String> {

}
