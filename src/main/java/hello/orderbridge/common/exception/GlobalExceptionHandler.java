package hello.orderbridge.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("[{}] {}", errorCode.getCode(), errorCode.getMessage());

        model.addAttribute("error", ErrorResponse.of(errorCode));
        return "error/business";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException e) {
        log.debug("정적 리소스 없음: {}", e.getResourcePath());
        return "error/business";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        log.error("예상치 못한 오류 발생: ", e);

        model.addAttribute("error", new ErrorResponse(
                "S001",
                "서버 내부 오류가 발생했습니다.",
                java.time.LocalDateTime.now()
        ));

        return "error/server";
    }
}
