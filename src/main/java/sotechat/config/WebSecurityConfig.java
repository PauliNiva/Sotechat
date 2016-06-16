package sotechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

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
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    /**
     * Maarittelee mm. kirjautumisvaatimuksen sivulle /pro.
     */
    @Override
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected final void configure(final HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http
                // "maaritellaan seuraavaksi, mitka
                // pyynnot vaativat kirjautumisen"
                .authorizeRequests().antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                // pyynnot polkuun /pro
                // vaativat kirjautumisen

                // muut pyynnot
                // sallitaan kaikille
                .and()
                // jotenkin kai yhdistaa
                // login-sivun session luomiseen
                .httpBasic().and().csrf()
                .csrfTokenRepository(csrfTokenRepository()).and()
                .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
                .logout().logoutSuccessUrl("/pro");

        // polun /login mappays
        // loytyy tiedostosta MvcConfig

        // paasy login-sivulle
        // sallitaan kaikille

        // logout sallitaan kaikille


        // TODO: allaoleva HTTP->HTTPS ohjaus ei toimi
        // http.requiresChannel().anyRequest().requiresSecure();

        // TODO: alla oleva csrf tokenin configurointi ei toimi
        // http.addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(),
        // CsrfFilter.class);

        /* Thymeleafilla on jokin rooli kirjautumista vaativien
         * pyyntojen uudelleenohjaamisessa login.html -sivulle.
          * TODO: Mitenkohan uudelleenohjaus tarkalleen ottaen toimii? */
    }

    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain)
                    throws ServletException, IOException
            {
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
        private CsrfTokenRepository csrfTokenRepository () {
            HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
            repository.setHeaderName("X-XSRF-TOKEN");
            return repository;
        }

        /** Kovakoodataan hoitajan tunnukset siihen saakka,
         * etta tietokanta on kaytossa.
         * @param auth mika tama on?
         * @throws Exception mika poikkeus?
         */
        @Autowired
        public final void configureGlobal ( final AuthenticationManagerBuilder auth)
        throws Exception {
            auth
                    .inMemoryAuthentication()
                    .withUser("Hoitaja")
                    .password("salasana")
                    .roles("USER").and()
                    .withUser("Hoitaja2")
                    .password("salasana")
                    .roles("USER");
            // TODO: selvita roolin merkitys.
        }
    }
