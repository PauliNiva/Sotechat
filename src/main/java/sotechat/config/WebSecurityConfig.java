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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import sotechat.auth.JpaAuthenticationProvider;

/**
 * Ottaa <code>Spring Security</code>:n kayttoon.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Maarittaa sallitut resurssit, CSRF-suojauksen, sekä sivun jolle
     * ohjataan uloskirjautumisen jalkeen.
     */
    @Override
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected final void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic().and().csrf().ignoringAntMatchers("/toServer/**")
                .csrfTokenRepository(csrfTokenRepository()).and()
                .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
                .headers().frameOptions().sameOrigin().and()
                .logout().logoutSuccessUrl("/pro");
    }

    /**
     * Luo Angular-yhteensopivan CSRF-filtterin.
     * @return Palauttaa CSRF-filterin.
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
     * Luo uuden <code>CsrfToken</code>:in.
     * Asettaa sen <code>Header</code> nimen Angular-yhteensopivaksi.
     * @return Palauttaa <code>CsrfTokenRepository</code>-olion.
     */
    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository =
                new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

    /**
     * Yhdistaa tietokannan <code>Spring Security</code> todentamiseen.
     */
    @Configuration
    protected static class AuthenticationConfiguration
            extends GlobalAuthenticationConfigurerAdapter {

        /**
         * <code>JpaAuthenticationProvider</code>-olio.
         */
        @Autowired
        private JpaAuthenticationProvider jpaAuthenticationProvider;

        /**
         * Kaynnistaa todentamispalvelun.
         * @param auth AuthenticationManagerBuilder.
         * @throws Exception Tietokantaan yhdistamisen epaonnistuessa.
         */
        @Override
        public final void init(final AuthenticationManagerBuilder auth)
                throws Exception {
            auth.authenticationProvider(jpaAuthenticationProvider);
        }
    }
}
