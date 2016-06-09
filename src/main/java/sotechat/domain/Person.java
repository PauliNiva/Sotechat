package sotechat.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;

@Entity
public class Person extends AbstractPersistable<Long> {

    private String screenName;

    @Column(unique = true)
    private String username;

    private String password;
    private String salt;

    @ManyToMany
    private List<Conversation> conversationsOfPerson;

    public Person() {
    }

    public final String getScreenName() {
        return screenName;
    }

    public final void setScreenName(final String pname) {
        this.screenName = pname;
    }

    public final String getUsername() {
        return username;
    }

    public final void setUsername(final String pusername) {
        this.username = pusername;
    }

    public final String getPassword() {
        return password;
    }

    public final void setPassword(final String pPassword) {
        this.salt = BCrypt.gensalt();
        this.password = BCrypt.hashpw(pPassword, this.salt);
    }

    public final String getSalt() {
        return salt;
    }

    public final void setSalt(final String psalt) {
        this.salt = psalt;
    }

    public final List<Conversation> getConversationsOfPerson() {
        return this.conversationsOfPerson;
    }

    public final void setConversationsOfPerson(
            final List<Conversation> pConversationsOfPerson) {
        this.conversationsOfPerson = pConversationsOfPerson;
    }

    public final void addConversationToPerson(
            final Conversation conversation) {
        this.conversationsOfPerson.add(conversation);
    }
}
