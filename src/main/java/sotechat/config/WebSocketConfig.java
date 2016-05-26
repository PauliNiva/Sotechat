package sotechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.
        AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.
        EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * Konfiguraatioluokka, joka määrittelee, että asiakasohjelmalta(clientiltä)
 * tulleet viestit kulkevat WebSocketin kautta palvelimelle
 * ChatController-luokan käsiteltäväksi. Lisäksi luokka välittää
 * ChatControllerin generoiman vastauksen asiakasohjelmalle.
 * @since 19.5.2016
 */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {

    /**
     * Metodi käyttää MessageBrokerRegistry-luokan metodia enableSimpleBroker
     * välittääkseen WebSocketin kautta asiakasohjelmalle viestin palvelimelta.
     * Palvelimen viestit tulevat ChatController-luokalta.
     *
     * @param conf Välittäjäolio, joka välittää viestit WebSocketin kautta
     *               palvelimen ChatController-luokalta asiakasohjelmalle.
     */
    @Override
    public final void configureMessageBroker(final MessageBrokerRegistry conf) {
        conf.enableSimpleBroker("/toClient", "/topic/");
    }

    /**
     * Metodi käyttää StompEndpointRegistry-luokan metodia addEndpoint
     * määrittääkseen asiakasohjelmalta WebSocketin kautta tulleille viesteille
     * pääteosoitteen. Tässä ohjelmassa viestit ohjautuvat
     * ChatController-luokalle.
     *
     * @param reg Luokka, joka määrittää WebSocketin kautta tulleille
     *                 asiakasohjelmien viesteille pääteosoitteen.
     */
    @Override
    public final void configureStompEndpoints(final StompEndpointRegistry reg) {
        reg.addEndpoint("/toServer").withSockJS();
    }

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyHandler(), "/myHandler")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }
}
