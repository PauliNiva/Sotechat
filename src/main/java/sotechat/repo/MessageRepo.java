package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Message;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {

    List<Message> findByConversation(String channelId);
}
