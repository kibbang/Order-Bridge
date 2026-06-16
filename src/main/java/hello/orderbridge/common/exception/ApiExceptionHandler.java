package hello.orderbridge.common.exception;

import hello.orderbridge.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "hello.orderbridge.order.controller.api")
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("[API][{}] {}", errorCode.getCode(), errorCode.getMessage());
        return ApiResponse.fail(errorCode.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("[API] 예상치 못한 오류: ", e);
        return ApiResponse.fail("서버 내부 오류가 발생했습니다.");
    }
}