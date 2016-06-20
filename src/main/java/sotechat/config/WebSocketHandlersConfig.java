package sotechat.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.session.ExpiringSession;
import sotechat.connectionEvents.ConnectionHandler;
import sotechat.connectionEvents.ConnectionRepo;
import sotechat.connectionEvents.WebSocketConnectHandler;
import sotechat.controller.QueueBroadcaster;
import sotechat.data.Queue;
import sotechat.data.SessionRepo;
import sotechat.connectionEvents.WebSocketDisconnectHandler;

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
    public ConnectionHandler connectionHandler(
            final ConnectionRepo connectionRepo,
            final SessionRepo sessionRepo,
            final Queue queue,
            final QueueBroadcaster queueBroadcaster) {
        return new ConnectionHandler(connectionRepo, sessionRepo,
                queue, queueBroadcaster);
    }

    /** Taytetaan myohemmin. Taytetaan myohemmin
     * @return taytetaan myohemmin
     */
    @Bean
    public WebSocketDisconnectHandler<S> webSocketDisconnectHandler(
            final ConnectionRepo pConnectionRepo,
            final ConnectionHandler pConnectionHandler) {
        return new WebSocketDisconnectHandler<S>(pConnectionRepo,
                pConnectionHandler);
    }

    @Bean
    public WebSocketConnectHandler<S> webSocketConnectHandler(
            final ConnectionRepo pConnectionRepo) {
        return new WebSocketConnectHandler<S>(pConnectionRepo);
    }
}
