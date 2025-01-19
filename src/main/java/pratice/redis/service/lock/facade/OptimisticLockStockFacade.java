package pratice.redis.service.lock.facade;


import org.springframework.stereotype.Component;
import pratice.redis.service.lock.StockService;

@Component
public class OptimisticLockStockFacade {

    private final StockService stockService;

    public OptimisticLockStockFacade(StockService stockService) {
        this.stockService = stockService;
    }

    //[해결책 3] : DB락 OptimisticLock 사용하기
    //-. Entity에 version 필드를 추가해줘야 한다. (@Version)
    //성능문제 + 데드락 가능성에 대해 자유로워 질 수 있었다.
    //[단점]
    //1. 실패에 대한 로직을 추가로 작성해줘야 한다.
    //2. 충돌이 빈번하게 발생되는 로직에서는 성능상 문제가 발생한다.
    public void decrease(Long id, long quantity) throws InterruptedException {
        while (true) {
            try {
                stockService.decreaseV4(id, quantity);
                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }

}
