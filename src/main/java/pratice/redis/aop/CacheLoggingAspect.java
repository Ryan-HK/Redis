package pratice.redis.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
public class CacheLoggingAspect {

    //현재 캐시가 정상적으로 잘 작동되고 있는지 체크한다.
    @Pointcut("@annotation(org.springframework.cache.annotation.Cacheable)")
    public void useCacheMethod(){}

    @Before("useCacheMethod()")
    public void beforeCache() {
        log.info(">>> Cacheable 사용 예정 입니다.");
    }

    @After("useCacheMethod()")
    public void AfterCache() {
        log.info(">>> Cacheable 사용 완료되었습니다.");
    }

}
