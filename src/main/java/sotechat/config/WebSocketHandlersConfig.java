package sotechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.session.ExpiringSession;
import sotechat.websocket.WebSocketConnectHandler;

@Configuration
public class WebSocketHandlersConfig<S extends ExpiringSession> {



    @Bean
    public WebSocketConnectHandler<S> webSocketConnectHandler(SimpMessageSendingOperations messagingTemplate) {
        return new WebSocketConnectHandler<S>(messagingTemplate);
    }
}
