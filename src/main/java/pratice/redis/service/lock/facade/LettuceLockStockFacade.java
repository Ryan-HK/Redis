package pratice.redis.service.lock.facade;


import org.springframework.stereotype.Component;
import pratice.redis.service.lock.RedisLockService;
import pratice.redis.service.lock.StockService;

@Component
public class LettuceLockStockFacade {

    private final RedisLockService redisLockService;
    private final StockService stockService;


    public LettuceLockStockFacade(RedisLockService redisLockService, StockService stockService) {
        this.redisLockService = redisLockService;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (!redisLockService.lock(id)) {
            Thread.sleep(100);
        }

        try {
            stockService.decreaseV1(id, quantity);
        } finally {
            redisLockService.unlock(id);
        }
    }
}
