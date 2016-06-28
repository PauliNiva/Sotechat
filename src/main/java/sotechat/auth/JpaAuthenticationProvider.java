package sotechat.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication
        .AuthenticationProvider;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import sotechat.data.Mapper;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;

@Component
public class JpaAuthenticationProvider implements AuthenticationProvider {

    /**
     * Mapper.
     */
    @Autowired
    private Mapper mapper;

    /**
     * PersonRepo.
     */
    @Autowired
    private PersonRepo personRepo;

    /**
     * Konstruktori.
     * @param a auth
     * @return TODO
     * @throws AuthenticationException TODO
     */
    @Override
    public final Authentication authenticate(final Authentication a)
            throws AuthenticationException {
        String loginName = a.getPrincipal().toString();
        String password = a.getCredentials().toString();
        Person person = personRepo.findByLoginName(loginName);

        if (person == null) {
            throw new AuthenticationException(
                    "Unable to authenticate user " + loginName) {
            };
        }
        if (!BCrypt.hashpw(password, person.getSalt())
                .equals(person.getPassword())) {
            throw new AuthenticationException(
                    "Unable to authenticate user " + loginName) {
            };
        }

        List<GrantedAuthority> grantedAuths = grantAuthority(person);
        mapper.mapProUsernameToUserId(person.getUserName(), person.getUserId());
        return new UsernamePasswordAuthenticationToken(person.getUserName(),
                password, grantedAuths);
    }

    /**
     * TODO
     * @param person person
     * @return TODO
     */
    private List<GrantedAuthority> grantAuthority(final Person person) {
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        if (person.getRole().equals("ROLE_ADMIN")) {
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return grantedAuths;
    }

    /**
     * TODO
     * @param type
     * @return
     */
    @Override
    public final boolean supports(final Class<?> type) {
        return true;
    }
}
