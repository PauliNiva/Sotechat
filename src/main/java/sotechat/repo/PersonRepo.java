package sotechat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sotechat.domain.Person;

public interface PersonRepo extends JpaRepository<Person, String> {

}

