package sotechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config
        .annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web
        .socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import sotechat.controller.SubscriptionInterceptor;
import sotechat.data.SessionRepo;
import sotechat.service.ValidatorService;


/** Palvelin kasittelee kahta erityyppista liikennetta: HTML ja WebSockets.
 * Tama konfiguraatioluokka koskee WebSocket-liikenteen kasittelya.
 * Maaritellaan polut, joihin tulevat/menevat viestit
 * kasitellaan - ja muihin polkuihin menevat viestit unohdetaan.
 * Lisaksi ohjataan subscriptionien hyvaksyminen interceptorille. */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig extends
        AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {

    /** SessionRepoImpl taytyy autowireaa tassa, jotta WebSocket-sessiot
     * onnistutaan sailomaan sinne. */
    @Autowired
    private SessionRepo repository;

    /** ValidatorService taytyy autowireaa tassa, jotta se voidaan antaa
     * parametrina luotavalle Interceptorille, joka on pakko maaritella tassa.*/
    @Autowired
    private ValidatorService validatorSer;

    /**
     * Olio, joka "kuuntelee" WebSocket-yhteyden kautta tapahtuvia kanaville
     * tehtyja listautumispyyntoja.
     */
    @Autowired
    private ApplicationListener<ApplicationEvent> subscribeEventListener;


    /** Metodi kayttaa MessageBrokerRegistry-luokan metodia enableSimpleBroker
     * valittaakseen WebSocketin kautta asiakasohjelmalle viestin palvelimelta.
     *
     * @param conf Valittajaolio, joka valittaa viestit WebSocketin kautta
     *               palvelimen ChatController-luokalta asiakasohjelmalle.
     */
    @Override
    public final void configureMessageBroker(
            final MessageBrokerRegistry conf
    ) {
        conf.enableSimpleBroker("/toClient");
    }

    /** Metodi kayttaa StompEndpointRegistry-luokan metodia addEndpoint
     * maarittaakseen asiakasohjelmalta WebSocketin kautta tulleille viesteille
     * paateosoitteen.
     *
     * @param reg Luokka, joka maarittaa WebSocketin kautta tulleille
     *                 asiakasohjelmien viesteille paateosoitteen.
     */
    @Override
    public final void configureStompEndpoints(
            final StompEndpointRegistry reg
    ) {
        reg.addEndpoint("/toServer").withSockJS();
    }

    /** Subscriptionien hyvaksyminen ohjataan Interceptor -instanssille.
     * @param registration registration
     */
    @Override
    public void configureClientInboundChannel(
            final ChannelRegistration registration
    ) {
        registration.setInterceptors(new SubscriptionInterceptor(validatorSer));
    }
}

