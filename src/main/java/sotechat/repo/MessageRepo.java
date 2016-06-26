package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Message;

public interface MessageRepo extends JpaRepository<Message, Long> {
}
