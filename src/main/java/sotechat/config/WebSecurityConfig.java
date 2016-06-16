package sotechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication
        .builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration
        .WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration
        .EnableWebSecurity;
import sotechat.auth.JpaAuthenticationProvider;

/** Tama konfiguraatiotiedosto ottaa Spring Securityn kayttoon
 *  yhdessa joidenkin pom.xml -maarityksien kanssa. */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    /** Maarittelee mm. kirjautumisvaatimuksen sivulle /pro. */
    @Override
    protected final void configure(final HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http
                // "maaritellaan seuraavaksi, mitka
                // pyynnot vaativat kirjautumisen"
                .authorizeRequests()
                // pyynnot polkuun /pro
                // vaativat kirjautumisen
                .antMatchers("/pro", "/proCP.html").authenticated()
                // muut pyynnot
                // sallitaan kaikille
                .anyRequest().permitAll()
                .and()
                // jotenkin kai yhdistaa
                // login-sivun session luomiseen
                .formLogin()
                // polun /login mappays
                // loytyy tiedostosta MvcConfig
                .loginPage("/login")
                // paasy login-sivulle
                // sallitaan kaikille
                .permitAll()
                .and()
                .logout()
                // logout sallitaan kaikille
                .permitAll();

        // TODO: allaoleva HTTP->HTTPS ohjaus ei toimi
        // http.requiresChannel().anyRequest().requiresSecure();

        // TODO: alla oleva csrf tokenin configurointi ei toimi
        // http.addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(),
        // CsrfFilter.class);

        /* Thymeleafilla on jokin rooli kirjautumista vaativien
         * pyyntojen uudelleenohjaamisessa login.html -sivulle.
          * TODO: Mitenkohan uudelleenohjaus tarkalleen ottaen toimii? */
    }

    @Configuration
    protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private JpaAuthenticationProvider jpaAuthenticationProvider;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(jpaAuthenticationProvider);
        }
    }
}
