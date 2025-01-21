package pratice.redis.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pratice.redis.service.message.RedisMessageListener;

@Configuration
public class RedisPubSubConfig {


    //RedisMessageListenerContainer
    // -. Redis의 Pub/Sub 메시지 수신을 담당하는 컨테이너
    // -. 하나 이상의 리스너를 등록하고, 특정 채널 또는 패턴을 구독
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        //Redis 연결 팩토리를 설정한다. -> Redis와의 연결을 생성하고 관리한다. (필수)
        container.setConnectionFactory(connectionFactory);

        //메시지를 처리할 리스너와 구독할 주제(채널 혹은 패턴)을 등록한다.
        // -. 패턴 형식
        container.addMessageListener(listenerAdapter, new PatternTopic("pratice:*"));
        // -. 채널형식
        container.addMessageListener(listenerAdapter, new ChannelTopic("event-channel"));

        //병렬처리를 위한 스레드풀 설정 가능 - 메시지 처리
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.initialize();
        container.setTaskExecutor(executor);

        //병렬처리를 위한 스레드풀 설정 - 채널에 대한 구독작업 처리
        // -. 많은 채널에 구독하거나 연결작업이 많은 경우, 구독 작업을 병렬로 처리한다.
        ThreadPoolTaskExecutor subscriptionExecutor = new ThreadPoolTaskExecutor();
        subscriptionExecutor.initialize();
        container.setSubscriptionExecutor(subscriptionExecutor);

        //Redis연결이 끊어졌을 때, 다시 연결 시도 간격
        // -. default 5000ms
        container.setRecoveryInterval(5000);

        //메세지 처리 중 예외 핸들러
        container.setErrorHandler(e -> {
            System.out.println("Error... invoke");
        });


        return container;
    }



    @Bean
    public MessageListenerAdapter listenerAdapter(RedisMessageListener messageListener) {
        return new MessageListenerAdapter(messageListener, "receiveMessage");
    }

}
