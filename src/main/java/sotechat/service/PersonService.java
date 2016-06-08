package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.repo.ConversationRepo;
import sotechat.repo.PersonRepo;

/**
 * Created by varkoi on 8.6.2016.
 */
@Service
public class PersonService {

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private ConversationRepo conversationRepo;
    

}
