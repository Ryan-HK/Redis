package pratice.redis.service.lock;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pratice.redis.domain.Stock;
import pratice.redis.repository.StockRepository;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    //동시성을 고려하지 않은 로직
    //-. Race Condition 문제 발생
    @Transactional
    public void decreaseV1(Long id, long quantity) {
        // Stock 조회
        Stock stock = stockRepository.findById(id).orElseThrow();
        // 재고 감소
        stock.decrease(quantity);
        // 갱신한 값을 저장
        stockRepository.saveAndFlush(stock);
    }

    //[해결책 1] : Java synchronized 이용해서 하나의 Thread가 해당 서비스에 접근하도록 한다.
    //[문제점]
    //1. @Transactional 을 사용하지 못한다.
    //-. 해당 어노테이션은 호출자에게 Proxy클래스를 제공하기 때문에,
    //synchronized가 우리가 원하는대로 작동하지 않는다.
    //2. 다중 Server에서는 작동하지 않는다.
    //[현업에서의 문제점]
    //-. 실제 운영하던 Admin서버는 다중 프로세스 환경에서 발생할 수 있는 문제는 해당하지 않았다.
    //하지만 실제 금융서비스는 여러개의 로직의 Transaction 단위로 묶여 데이터 무결성을 가져야 하기 때문에
    //해당 해결책은 적용하지 않았다.
    public synchronized void decreaseV2(Long id, long quantity) {
        // Stock 조회
        Stock stock = stockRepository.findById(id).orElseThrow();
        // 재고 감소
        stock.decrease(quantity);
        // 갱신한 값을 저장
        stockRepository.saveAndFlush(stock);
    }


    //[해결책 2] : DB락 PessimisticLock Write를 이용하기
    //-. @Transactional을 다시 사용할 수 있어 여러 로직을 하나의 트랜잭션안에서 관리가 가능해졌다.
    //[현업에서의 문제점]
    //1-1. MyBatis와 JPA의 차이
    //    MyBatis에서는 직접 SQL을 작성하여 FOR UPDATE를 명시해야 락을 걸 수 있음.
    //    JPA는 코드 상에서 간단히 락을 선언적으로 관리할 수 있지만, 동일한 기능을 위해 MyBatis와 JPA에서 접근 방식이 달라 관리 복잡성이 증가할 수 있음.
    //1-2. 충돌이 빈번한 경우의 성능
    //    Pessimistic Lock은 충돌이 빈번한 환경에서 효과적으로 동시성 문제를 해결할 수 있음.
    //    그러나 락이 길게 유지되거나 요청량이 많은 환경에서는 성능 저하 및 데드락 가능성이 증가함.
    //1-3. 복잡한 트랜잭션의 경우
    //    특정 엔티티 하나에 락을 거는 방식은 단순한 동시성 제어에는 적합하지만, 다수의 테이블을 조합해 새로운 데이터를 생성해야 하는 정산 로직에는 한계가 있음.
    //    이 경우 트랜잭션 범위가 넓어져 트랜잭션 관리의 복잡성 및 성능 문제가 발생할 수 있음. 데이터로 하나의 데이터를 만들어 단 하나의 INSERT를 해야하는 정산로직에서는 적절하지 못했다.
    @Transactional
    public void decreaseV3(Long id, long quantity) {

        Stock stock = stockRepository.findByWithPessimisticLock(id);
        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);

    }

    //[해결책 3] : DB락 OptimisticLock 사용하기
    //-. Entity에 version 필드를 추가해줘야 한다. (@Version)
    //성능문제 + 데드락 가능성에 대해 자유로워 질 수 있었다.
    //[특징]
    //1. 실패에 대한 로직을 추가로 작성해줘야 한다. -> @Transactional을 유지해줘야 하기 때문에 별도의 Facade 클래스가 필요하다.
    //2. 충돌이 빈번하게 발생되는 로직에서는 성능상 문제가 발생한다.
    @Transactional
    public void decreaseV4(Long id, long quantity) throws InterruptedException {
        // Stock 조회
        Stock stock = stockRepository.findById(id).orElseThrow();
        // 재고 감소
        stock.decrease(quantity);
        // 갱신한 값을 저장
        stockRepository.saveAndFlush(stock);
    }


    //[해결책 4] : Named 락
    //별도의 락을 거는 방식으로 간다.
    //-. 내가 원하는 정산 로직전체에 락을 걸 수 있다.
    //부모클래스와는 별도의 트랜잭션을 사용해야하기 때문에 REQUIRES_NEW 전파 옵션을 사용한다.
    //-. 별도의 트랜잭션을 가진다.
    //[특징]
    //1. DB 락을 get하고 release 하는 로직이 추가되어야 한다. -> 별도의 트랜잭션을 유지하기 위해 Propagetion 이 필요하다.
    //2. 해당 락을 사용하려면 별도의 DB를 사용해야 한다. (커넥션 문제)
    //3. DB커넥션을 점유하기 때문에, 다른 서비스에 영향을 끼칠 수 있다.
    //4. 해당 방법을 사용하기 보다는 Redis를 이용한 분산락을 사용하는 것이 더욱 바람직한것으로 보인다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseV5(Long id, long quantity)  {
        // Stock 조회
        Stock stock = stockRepository.findById(id).orElseThrow();
        // 재고 감소
        stock.decrease(quantity);
        // 갱신한 값을 저장
        stockRepository.saveAndFlush(stock);
    }



}
