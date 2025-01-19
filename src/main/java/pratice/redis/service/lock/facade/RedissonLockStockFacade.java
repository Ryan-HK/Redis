package pratice.redis.service.lock.facade;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import pratice.redis.service.lock.StockService;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class RedissonLockStockFacade {

    private final RedissonClient redissonClient;
    private final StockService stockService;


    public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) {
        RLock lock = redissonClient.getLock(id.toString());

        try {

            //Lock 획득
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                log.info(">>> lock 획득 실패");
                return;
            }

            //서비스 로직 실행
            stockService.decreaseV1(id, quantity);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //Lock 해제
            lock.unlock();
        }
    }

}
