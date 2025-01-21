package pratice.redis.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {


    //Message Broker로 STOMP를 활성화
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //Broker로 Redis를 설정할 예정이다. - Client가 구독할 브로커 경로
        registry.enableSimpleBroker("/topic", "/queue");

        //클라이언트가 서버로 메시지 전송 시, 사용할 경로 prefix
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp-endpoint")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

}
