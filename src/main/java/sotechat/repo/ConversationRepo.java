package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Conversation;

public interface ConversationRepo extends JpaRepository<Conversation, String> {

}
