package sotechat.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.ExpiringSession;
import sotechat.connectionEvents.ConnectionHandler;
import sotechat.connectionEvents.ConnectionRepo;
import sotechat.connectionEvents.WebSocketConnectHandler;
import sotechat.controller.QueueBroadcaster;
import sotechat.data.SessionRepo;
import sotechat.connectionEvents.WebSocketDisconnectHandler;
import sotechat.service.QueueService;

/**
 * Konfiguraatioluokka, jossa luodaan Springin käyttöön Beanit, jotka
 * vastaavat siitä, mitä tapahtuu, kun WebSocket-yhteys katkeaa, ja mitä
 * tapahtuu, kun yhteys muodostuu.
 * @param <S>
 */
@Configuration
public class WebSocketHandlersConfig<S extends ExpiringSession> {

    /**
     * Katso ConnectionRepo-luokasta tarkempi kuvaus.
     *
     * @return Palautetaan ConnectionRepo-olio Beanina Springin käyttöön.
     */
    @Bean
    public ConnectionRepo connectionRepo() {
        return new ConnectionRepo();
    }

    /**
     * Katso ConnectionHandler-luokasta tarkempi kuvaus.
     *
     * @return Palautetaan ConnectionHandler-olio Beanina Springin käyttöön.
     */
    @Bean
    public ConnectionHandler connectionHandler() {
        return new ConnectionHandler();
    }

    /**
     * Katso WebSocketDisconnectHandler-luokasta tarkempi kuvaus.
     *
     * @return Palautetaan WebSocketDisconnectHandler-olio Beanina Springin
     * käyttöön.
     */
    @Bean
    public WebSocketDisconnectHandler<S> webSocketDisconnectHandler() {
        return new WebSocketDisconnectHandler<S>();
    }

    /**
     * Katso WebSocketConnectHandler-luokasta tarkempi kuvaus.
     *
     * @return Palautetaan WebSocketConnectHandler-olio Beanina Springin
     * käyttöön.
     */
    @Bean
    public WebSocketConnectHandler<S> webSocketConnectHandler() {
        return new WebSocketConnectHandler<S>();
    }
}
