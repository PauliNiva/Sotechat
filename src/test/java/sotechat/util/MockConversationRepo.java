package sotechat.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sotechat.domain.Conversation;
import sotechat.repo.ConversationRepo;

import java.util.List;

public class MockConversationRepo implements ConversationRepo {

    private Map<>

    @Override
    public <S extends T> S save(S s) {
        return null;
    }

    @Override
    public Conversation findOne(String s) {
        return null;
    }

    @Override
    public boolean exists(String s) {
        return false;
    }

    @Override
    public List<Conversation> findAll() {
        return null;
    }

    @Override
    public List<Conversation> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Conversation> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Conversation> findAll(Iterable<String> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void delete(Conversation conversation) {

    }

    @Override
    public void delete(Iterable<? extends Conversation> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void flush() {

    }

    @Override
    public void deleteInBatch(Iterable<Conversation> iterable) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Conversation getOne(String s) {
        return null;
    }

    @Override
    public <S extends T> S saveAndFlush(S s) {
        return null;
    }

    @Override
    public <S extends T> List<S> save(Iterable<S> iterable) {
        ;
    }
}
