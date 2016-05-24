package sotechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/** Tämä konfiguraatiotiedosto ottaa Spring Securityn käyttöön
 *  yhdessä joidenkin pom.xml -määrityksien kanssa. */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    /** Määrittelee kirjautumisvaatimuksen sivulle /pro. */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // "määritellään seuraavaksi, mitkä pyynnöt vaativat kirjautumisen"
                .antMatchers("/pro").authenticated() // pyynnöt polkuun /pro vaativat kirjautumisen
                .anyRequest().permitAll() // muut pyynnöt sallitaan kaikille
                .and()
                .formLogin() // yhdistää login-sivun jotenkin session luomiseen tai jotain
                .loginPage("/login") // polun /login mäppäys löytyy tiedostosta MvcConfig
                .permitAll() // pääsy login-sivulle sallitaan kaikille
                .and()
                .logout()
                .permitAll(); // logout sallitaan kaikille

        /* Ilmeisesti tämä myös ohjaa jollain magialla
         * kirjautumista vaativat pyynnöt login.html -sivulle */
    }

    /** Kovakoodataan hoitajan tunnukset siihen saakka, että tietokanta on käytössä. */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("hoitaja").password("salasana").roles("USER"); //TODO: selvitä roolin merkitys
    }
}