package pratice.redis.service.message;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    //StringRedisTemplate는 문자열 처리에 특화된 레디스
    // -. 자동으로 StringRedisSerializer가 설정되어 있다.
    private final StringRedisTemplate redisTemplate;

    public void publishEvent(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
        System.out.println(">>> channel = " + channel + ", message = " + message);
    }



}
