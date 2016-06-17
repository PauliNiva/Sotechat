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

import sotechat.domain.Person;
import sotechat.repo.PersonRepo;

@Component
public class JpaAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private PersonRepo personRepo;

    @Override
    public Authentication authenticate(Authentication a)
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

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return new UsernamePasswordAuthenticationToken(person.getLoginName(),
                password, grantedAuths);
    }

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }
}