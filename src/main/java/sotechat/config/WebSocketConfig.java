package sotechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web
        .socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import sotechat.data.SessionRepoImpl;


/** Palvelin käsittelee kahta erityyppistä liikennettä: HTML ja WebSockets.
 * Tämä konfiguraatioluokka koskee WebSocket-liikenteen käsittelyä.
 * Ilmeisesti tässä määritellään polut, joihin tulevat/menevät viestit
 * käsitellään - ja muihin polkuihin menevät viestit unohdetaan. */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig extends
        AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {

    /**
     * Session Repository.
     */
    @Autowired
    private SessionRepoImpl repository;


    /** Metodi käyttää MessageBrokerRegistry-luokan metodia enableSimpleBroker
     * välittääkseen WebSocketin kautta asiakasohjelmalle viestin palvelimelta.
     * Palvelimen viestit tulevat ChatController-luokalta.
     *
     * @param conf Välittäjäolio, joka välittää viestit WebSocketin kautta
     *               palvelimen ChatController-luokalta asiakasohjelmalle.
     */
    @Override
    public final void configureMessageBroker(final MessageBrokerRegistry conf) {
        conf.enableSimpleBroker("/toClient");
    }

    /** Metodi käyttää StompEndpointRegistry-luokan metodia addEndpoint
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
}
