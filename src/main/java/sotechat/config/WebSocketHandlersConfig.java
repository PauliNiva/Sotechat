package sotechat.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.ExpiringSession;
import sotechat.connectionEvents.QueueTimeout;
import sotechat.connectionEvents.ConnectionRepo;
import sotechat.connectionEvents.WebSocketConnectHandler;
import sotechat.connectionEvents.WebSocketDisconnectHandler;

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
     * Katso QueueTimeout-luokasta tarkempi kuvaus.
     *
     * @return Palautetaan QueueTimeout-olio Beanina Springin käyttöön.
     */
    @Bean
    public QueueTimeout connectionHandler() {
        return new QueueTimeout();
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
