package sotechat.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
public class Person extends AbstractPersistable<Long> {

    private String name;

    @Column(unique = true)
    private String username;

    private String password;
    private String salt;

    public Person() {
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String pname) {
        this.name = pname;
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
}
