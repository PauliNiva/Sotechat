package sotechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.
        ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.
        WebMvcConfigurerAdapter;

/**
 * Maarittelee kayttoliittyman tiedostopolun.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    /**
     * Staattisten resurssien polku /webapp/.
     */
    @Override
    public final void addResourceHandlers(final ResourceHandlerRegistry reg) {
        reg.addResourceHandler("/resources/**")
                .addResourceLocations("\"classpath:/webapp/\"");
    }
}
