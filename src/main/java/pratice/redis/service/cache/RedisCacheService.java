package pratice.redis.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pratice.redis.domain.Stock;
import pratice.redis.repository.StockRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final StockRepository stockRepository;

    //Cache Around 전략
    // -. L2 Cache를 조회하여 데이터가 있으면 Cache Hit / 없으면 Cache Miss
    //Key 설정
    // SqEL (Spring Expression Language)를 사용해서 키를 지정한다.
    //-. #root.methodName : 메서드 이름
    //-. #root.targetClass.simpleName : 현재 클래스 심플네임
    //-. #id : 메서드의 파라미터 이름
    @Cacheable(
            value = "shortCache",
            key = "#root.targetClass.simpleName + ':' + #root.methodName + ':' + #id"
    )
    @Transactional
    public Stock useCache(long id) {
        Optional<Stock> byId =
                stockRepository.findById(id);

        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new RuntimeException();
        }
    }

    @Transactional
    public Stock noneCache(long id) {
        Optional<Stock> byId =
                stockRepository.findById(id);

        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new RuntimeException();
        }
    }

    //@CacheEvict
    //-. 캐시에서 데이터를 삭제한다.
    //-. 데이터가 변경되거나 삭제될 때, 해당 데이터와 관련된 캐시를 제거해서 데이터베이스와 캐시 간의 일관성 유지
    @CacheEvict(
            value = "shortCache",
            key = "#root.targetClass.simpleName + ':' + 'useCache' + ':' + #id",
            beforeInvocation = true
    )
    public void deleteCache(long id) {
        log.info(">>> delete Cache : {}", id);
    }

}
