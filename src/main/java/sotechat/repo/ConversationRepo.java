package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Conversation;

/**
 * <code>JPA</code>-sailo, joka toimii sovelluksen ja tietokannan valisena
 * rajapintana. Sailo mahdollistaa <code>Conversation</code>-olioiden
 * tallentamisen tietokantaan ja vastaavasti rivien hakemisen tietokannan
 * <code>Conversation</code>-taulusta ja naiden tallentamisen
 * <code>Conversation</code>-olioiksi.
 */
public interface ConversationRepo extends JpaRepository<Conversation, String> {

}
