package pratice.redis.service.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisPublisherTest {

    @Autowired
    private RedisPublisher redisPublisher;

    @Test
    void publish() {
        redisPublisher.publishEvent("event-channel", "this message send at server");
    }

}