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
 * Keskenerainen.
 * @param <S>
 */
@Configuration
public class WebSocketHandlersConfig<S extends ExpiringSession> {

    @Bean
    public ConnectionRepo connectionRepo() {
        return new ConnectionRepo();
    }

    @Bean
    public ConnectionHandler connectionHandler() {
        return new ConnectionHandler();
    }

    /** Taytetaan myohemmin. Taytetaan myohemmin
     * @return taytetaan myohemmin
     */
    @Bean
    public WebSocketDisconnectHandler<S> webSocketDisconnectHandler() {
        return new WebSocketDisconnectHandler<S>();
    }

    @Bean
    public WebSocketConnectHandler<S> webSocketConnectHandler() {
        return new WebSocketConnectHandler<S>();
    }
}
