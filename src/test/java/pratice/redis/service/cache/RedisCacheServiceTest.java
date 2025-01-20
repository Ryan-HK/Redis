package pratice.redis.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pratice.redis.domain.Stock;
import pratice.redis.repository.StockRepository;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class RedisCacheServiceTest {

    @Autowired
    RedisCacheService redisCacheService;
    @Autowired
    StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        stockRepository.saveAndFlush(new Stock(1L, 100));
        stockRepository.saveAndFlush(new Stock(2L, 100));
    }


    @Test
    void _캐시_사용() {
        for(int i = 0; i < 1000; i++){
            redisCacheService.useCache(1L);
        }
    }

    @Test
    void _캐시_사용안함() {
        for(int i = 0; i < 1000; i++){
            redisCacheService.noneCache(1L);
        }
    }

    @Test
    void _캐시_삭제_테스트() {
        for(int i = 0; i < 1000; i++){
            redisCacheService.useCache(1L);
            redisCacheService.useCache(2L);

            if (i == 500) {
                redisCacheService.deleteCache(1L);
            }
        }
    }

}