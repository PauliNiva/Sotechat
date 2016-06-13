package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Conversation;
import sotechat.domain.Message;

import java.util.List;

public interface ConversationRepo extends JpaRepository<Conversation, String> {


}
