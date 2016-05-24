package sotechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.
        ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.
        WebMvcConfigurerAdapter;

/**
 * Luokka, joka määrittelee angularJS-tiedostojen tiedostopolun
 * Springin löydettäväksi.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public final void addResourceHandlers(final ResourceHandlerRegistry reg) {
        reg.addResourceHandler("/resources/**")
                .addResourceLocations("\"classpath:/webapp/\"");
    }
}
