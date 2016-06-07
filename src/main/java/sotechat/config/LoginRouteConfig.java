package sotechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation
        .WebMvcConfigurerAdapter;

/** Luokka ohjaa /login pyynnot /resources/templates/login.html.
 * Tekee myos jotain Spring Security -magiaa? */
@Configuration
public class LoginRouteConfig extends WebMvcConfigurerAdapter {

    /** Thymeleaf on jotenkin osallisena tassa login-mappayksessa.
     * @param registry tayta kuvaus
     */
    @Override
    public final void addViewControllers(
            final ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

}
