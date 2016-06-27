package sotechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation
        .authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration
        .EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation
        .web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation
        .web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import org.springframework.security.config.annotation
        .authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import sotechat.auth.JpaAuthenticationProvider;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Tama konfiguraatiotiedosto ottaa Spring Securityn kayttoon
 * yhdessa joidenkin pom.xml -maarityksien kanssa.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Määrittelee sallitut resurssit.
     * Csrf suojauksen, sekä sivun jolle
     * ohjataan uloskirjautumisen jälkeen
     */
    @Override
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected final void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic().and().csrf()
                .csrfTokenRepository(csrfTokenRepository()).and()
                .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
                .logout().logoutSuccessUrl("/pro");

        // TODO Allaoleva HTTP:stä HTTPS:ään ohjaus.
        // http.requiresChannel().anyRequest().requiresSecure();
    }

    /**
     * Luo angular yhteensopivan csrf filterin.
     * @return palauttaa csrf Filterin
     */
    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    final HttpServletRequest request,
                    final HttpServletResponse response,
                    final FilterChain filterChain)
                    throws ServletException, IOException {

                CsrfToken csrf = (CsrfToken) request
                        .getAttribute(CsrfToken.class.getName());
                if (csrf != null) {
                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                    String token = csrf.getToken();
                    if (cookie == null || token != null
                            && !token.equals(cookie.getValue())) {
                        cookie = new Cookie("XSRF-TOKEN", token);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    /**
     * Luodaan uusi CsrfToken.
     * Asetetaan sen Header nimeksi Angular yhteensopiva.
     * @return palauttaa csrfTokenRepositorin
     */
    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository =
                new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

    /**
     * Yhdistää tietokannan Spring security authentikointiin.
     */
    @Configuration
    protected static class AuthenticationConfiguration
            extends GlobalAuthenticationConfigurerAdapter {

        /**
         * Spring injektoi jpaAuthenticationProviderin tähän luokkaan.
         */
        @Autowired
        private JpaAuthenticationProvider jpaAuthenticationProvider;

        /**
         * Käynnistää jpa authentikointi palvelun.
         * @param auth AuthenticationManagerBuilder
         * @throws Exception Liittäminen securityyn ei onnistu
         */
        @Override
        public final void init(final AuthenticationManagerBuilder auth)
                throws Exception {
            auth.authenticationProvider(jpaAuthenticationProvider);
        }
    }
}
