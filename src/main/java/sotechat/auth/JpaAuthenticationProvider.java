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

/**
 * Luokka <code>JPA</code>-pohjaiseen kayttajan todentamiseen.
 * Koskee vain ammattilais- ja <code>admin</code>-kayttajia.
 * Toteuttaa rajapinnan <code>AuthenticationProvider</code>.
 */
@Component
public class JpaAuthenticationProvider implements AuthenticationProvider {

    /**
     * <code>Mapper</code>-olio.
     */
    @Autowired
    private Mapper mapper;

    /**
     * Säilö <code>person</code>-olioille.
     */
    @Autowired
    private PersonRepo personRepo;

    /**
     * Todentaa kayttajan tarkistamalla, etta kirjautuvan kayttajan salasana
     * vastaa <code>PersonRepo</code>:ssa tallessa olevaa salasanaa.
     *
     * @param auth <code>Authentication</code>-olio.
     * @return Valtuus, jossa argumentteina <code>person</code>-olion
     * kayttajanimi, salasana ja lista annetuistavaltuuksista.
     * @throws AuthenticationException Heitettava poikkeus jos todentaminen
     * epaonnistuu. Heitetaan kayttajan ollessa <code>null</code> tai salasana
     * ei vastaa tallessa olevaa salasanaa.
     */
    @Override
    public final Authentication authenticate(final Authentication auth)
            throws AuthenticationException {
        String loginName = auth.getPrincipal().toString();
        String password = auth.getCredentials().toString();
        Person person = personRepo.findByLoginName(loginName);

        if (person == null) {
            throw new AuthenticationException(
                    "Unable to authenticate user " + loginName) {
            };
        }
        if (!BCrypt.hashpw(password, person.getSalt())
                .equals(person.getHashOfPasswordAndSalt())) {
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
     * Antaa kayttajalle valtuudeksi joko roolin "ADMIN" tai roolin "USER".
     * Rooli "USER" viittaa rekisteroityyn ammattilaiskayttajaan.
     *
     * @param person Kirjautuva henkilo.
     * @return Kayttajan rooli listana.
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
     * Palauttaa <code>true</code>, jos <code>AuthenticationProvider</code>
     * tukee viitattua <code>Authentication</code>-oliota.
     * TODO: Selvennys, tuetaanko muitakin kuin Principal-tyyppisiä olioita?
     * <p>
     * <code>true</code>:n palautus ei takaa, etta
     * <code>AuthenticationProvider</code> pystyy valtuuttamaan sille esitetyn
     * <code>Authentication</code>-luokan ilmentyman.
     * <code>AuthenticationProvider</code> voi edelleen palauttaa arvon
     * <code>null</code>, vaikka <code>supports</code>-metodi palauttaisi
     * <code>true</code>. Talloin on kokeiltava toista
     * <code>AuthenticationProvider</code>:ia.
     * @param type tyyppi.
     * @return <code>true</code>, jos viitattu olio on tuettu.
     */
    @Override
    public final boolean supports(final Class<?> type) {
        return true;
    }

}
