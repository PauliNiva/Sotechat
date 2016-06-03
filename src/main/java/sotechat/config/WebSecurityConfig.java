package sotechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication
        .builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration
        .WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration
        .EnableWebSecurity;

/** Tämä konfiguraatiotiedosto ottaa Spring Securityn käyttöön
 *  yhdessä joidenkin pom.xml -määrityksien kanssa. */


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    /** Määrittelee mm. kirjautumisvaatimuksen sivulle /pro. */
    @Override
    protected final void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                // "määritellään seuraavaksi, mitkä
                // pyynnöt vaativat kirjautumisen"
                .authorizeRequests()
                // pyynnöt polkuun /pro
                // vaativat kirjautumisen
                .antMatchers("/pro", "/proCP.html").authenticated()
                // muut pyynnöt
                // sallitaan kaikille
                .anyRequest().permitAll()
                .and()
                // jotenkin kai yhdistää
                // login-sivun session luomiseen
                .formLogin()
                // polun /login mäppäys
                // löytyy tiedostosta MvcConfig
                .loginPage("/login")
                // pääsy login-sivulle
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
         * pyyntöjen uudelleenohjaamisessa login.html -sivulle.
          * TODO: Mitenköhän uudelleenohjaus tarkalleen ottaen toimii? */
    }

    /** Kovakoodataan hoitajan tunnukset siihen saakka,
     * että tietokanta on käytössä.
     * @param auth mikä tämä on?
     * @throws Exception mikä poikkeus?
     */
    @Autowired
     public final void configureGlobal(
            final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("Hoitaja")
                .password("salasana")
                .roles("USER");
                // TODO: selvitä roolin merkitys.
    }
}
