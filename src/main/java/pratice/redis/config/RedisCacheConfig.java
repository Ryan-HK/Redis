package pratice.redis.config;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@EnableCaching
@Configuration
public class RedisCacheConfig {


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))   // 기본 TTL 설정
                // Null값 캐싱 비활성화
                .disableCachingNullValues()
                // Key - Value 직렬화
                // -. Redis는 기본적으로 Byte로 값을 저장
                // -. 사람이 읽기 쉬운 형태로 저장한다.
                // -. 추천하는 세팅은 Key는 String / Value는 JSON형식이다.
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("shortCache", defaultConfig.entryTtl(Duration.ofSeconds(10)));
        cacheConfigurations.put("longCahce", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        //CacheManager 생성
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                //트랜잭션 내에서 캐싱
                // -. 롤백이 발생하면 캐시도 롤백하는 설정
                .transactionAware()
                .build();
    }

}
