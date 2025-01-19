package pratice.redis.service.facade;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pratice.redis.repository.LockRepository;
import pratice.redis.service.StockService;

@Component
public class NamedLockStockFacade {

    private final LockRepository lockRepository;
    private final StockService stockService;


    public NamedLockStockFacade(LockRepository lockRepository, StockService stockService) {
        this.lockRepository = lockRepository;
        this.stockService = stockService;
    }

    @Transactional
    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            stockService.decreaseV5(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }
}
