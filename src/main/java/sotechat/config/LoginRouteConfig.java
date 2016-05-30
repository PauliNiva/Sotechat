package sotechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation
        .WebMvcConfigurerAdapter;

/** Tämä luokka mappaa requestin /login oletuspolkuun
 *  resources/templates/login.html. (Mikään tapa
 *  määritellä login-resurssin löytymistä webapp
 *  -kansiosta ei toiminut.)
 *  (TODO: Selkeämpi mäppäys (myös index.html,
 *  joka löytyy nyt jotenkin maagisesti). */
@Configuration
public class LoginRouteConfig extends WebMvcConfigurerAdapter {

    @Override
    public final void addViewControllers(
            final ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

}
