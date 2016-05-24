package sotechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.
        ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.
        WebMvcConfigurerAdapter;

/**
 * Määrittelee Springille fronttipuolen tiedostopolun.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    /** Määrittelee, että /resources/ pyyntöihin haetaan
     *  tiedostot todellisesta polusta /webapp/. */
    @Override
    public final void addResourceHandlers(final ResourceHandlerRegistry reg) {
        reg.addResourceHandler("/resources/**")
                .addResourceLocations("\"classpath:/webapp/\"");
        // Näköjään toimii. Miksi polku vaatii nuo escapetut hipsut?
    }
}
