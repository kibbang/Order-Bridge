package hello.orderbridge.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect   // 이 클래스가 AOP 관점(Aspect)임을 선언 — 횡단 관심사(로깅)를 담당
@Component // Spring Bean으로 등록해야 AOP가 동작함
public class LoggingAspect {

    // @Around: 대상 메서드의 실행 "전후"를 모두 감싸는 어드바이스
    // - @Before: 실행 전에만 / @After: 실행 후에만 / @Around: 전+후 모두
    // - 여기서는 성공/실패를 모두 잡아야 하므로 @Around 사용
    //
    // execution(* hello.orderbridge..service..*(..)) 포인트컷 해석:
    //   *                          → 리턴 타입 상관없음
    //   hello.orderbridge..        → hello.orderbridge 하위 모든 패키지
    //   service..                  → service 패키지 및 그 하위 패키지
    //   *(..)                      → 모든 메서드, 파라미터 상관없음
    // 결과: OrderService, ClaimService, WmsService 등 모든 서비스 메서드에 적용
    @Around("execution(* hello.orderbridge..service..*(..))")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // joinPoint = AOP가 가로챈 실제 메서드에 대한 정보를 담고 있는 객체
        String methodName = joinPoint.getSignature().toShortString(); // ex) "OrderService.getOrder(..)"
        Object[] args = joinPoint.getArgs(); // 메서드에 전달된 실제 파라미터 값들

        try {
            // proceed() = 실제 원본 메서드를 실행 (이걸 호출 안 하면 원본이 실행되지 않음)
            Object result = joinPoint.proceed();
            log.debug("[SUCCESS] {} | [args = {}]", methodName, Arrays.toString(args));
            return result; // 원본 메서드의 리턴값을 그대로 반환
        } catch (Throwable e) {
            // 원본 메서드에서 예외가 발생하면 여기서 잡아서 로깅
            log.error("[ERROR] {} | [args = {}] | exception={} : {}",
                    methodName, Arrays.toString(args),
                    e.getClass().getSimpleName(), e.getMessage());
            throw e; // 로깅만 하고 예외는 다시 던짐 → GlobalExceptionHandler가 최종 처리
        }
    }
}
