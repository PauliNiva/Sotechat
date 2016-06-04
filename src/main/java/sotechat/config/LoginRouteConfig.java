package sotechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation
        .WebMvcConfigurerAdapter;

/** Luokka ohjaa /login pyynnöt /resources/templates/login.html.
 * Tekee myös jotain Spring Security -magiaa? */
@Configuration
public class LoginRouteConfig extends WebMvcConfigurerAdapter {

    /** Thymeleaf on jotenkin osallisena tässä login-mäppäyksessä.
     * @param registry täytä kuvaus
     */
    @Override
    public final void addViewControllers(
            final ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

}
