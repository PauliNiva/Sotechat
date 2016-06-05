package sotechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.
        ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.
        WebMvcConfigurerAdapter;

/** Maarittelee Springille fronttipuolen tiedostopolun.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    /** Maarittelee, etta /resources/ pyyntoihin haetaan
     *  tiedostot todellisesta polusta /webapp/. */
    @Override
    public final void addResourceHandlers(final ResourceHandlerRegistry reg) {
        reg.addResourceHandler("/resources/**")
                .addResourceLocations("\"classpath:/webapp/\"");
        /* Nakojaan toimii. Miksi polku vaatii nuo escapetut hipsut? */
    }
}
