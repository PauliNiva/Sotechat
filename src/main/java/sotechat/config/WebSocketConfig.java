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


/**
 * <code>WebSocket</code>-liikenteen asetukset.
 * Maarittelee sallitut polut liikenteelle ja
 * ohjaa tilausten hyvaksymisen <code>Interceptor</code>-oliolle.
 */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig extends
        AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {

    /**
     * Sailo erilaisille <code>Session</code>-olioille.
     * HUOMAUTUS <code>@Autowired</code>-notaatio ja luokkamuuttuja ovat
     * pakollisia tassa.
     */
    @Autowired
    private SessionRepo repository;

    /**
     * <code>ValidatorService</code>-olio.
     */
    @Autowired
    private ValidatorService validatorSer;

    /**
     * Olio, joka "kuuntelee" WebSocket-yhteyden kautta tapahtuvia kanaville
     * tehtyja listautumispyyntoja. //TODO fix
     */
    @Autowired
    private ApplicationListener<ApplicationEvent> subscribeEventListener;


    /**
     * Maarittelee sallitut polut <code>client</code>:in suuntaan.
     *
     * @param conf <code>MessageBrokerRegistry</code>-olio.
     */
    @Override
    public final void configureMessageBroker(
            final MessageBrokerRegistry conf
    ) {
        conf.enableSimpleBroker("/toClient");
    }

    /**
     * Maarittelee sallitut polut <code>server</code>:in suuntaan.
     *
     * @param reg <code>StompEndpointRegistry</code>-olio.
     */
    @Override
    public final void configureStompEndpoints(
            final StompEndpointRegistry reg
    ) {
        reg.addEndpoint("/toServer").withSockJS();
    }

    /**
     * Tilausten hyvaksyminen siirretaan <code>Interceptor</code>-oliolle.
     *
     * @param registration <code>ChannelRegistration</code>-olio.
     */
    @Override
    public void configureClientInboundChannel(
            final ChannelRegistration registration
    ) {
        registration.setInterceptors(new SubscriptionInterceptor(validatorSer));
    }
}
