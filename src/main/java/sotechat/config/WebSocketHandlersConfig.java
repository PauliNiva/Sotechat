package sotechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.session.ExpiringSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import sotechat.websocket.WebSocketConnectHandler;

/**
 * Keskeneräinen.
 * @param <S>
 */
@Configuration
public class WebSocketHandlersConfig<S extends ExpiringSession> {

    /** Täytetään myöhemmin.
     * @param messagingTemplate täytetään myöhemmin
     * @return täytetään myöhemmin
     */
    @Bean
    public WebSocketConnectHandler<S> webSocketConnectHandler(SimpMessageSendingOperations messagingTemplate) {
        return new WebSocketConnectHandler<S>(messagingTemplate);
    }
}
