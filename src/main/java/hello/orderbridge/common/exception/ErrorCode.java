package hello.orderbridge.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 주문
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),

    // 주문 아이템
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "OI001", "주문 상품을 찾을 수 없습니다."),

    // 클레임
    CLAIM_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "클레임을 찾을 수 없습니다."),
    CLAIM_ALREADY_EXISTS(HttpStatus.CONFLICT, "C002", "이미 클레임이 접수된 상품입니다."),

    // WMS
    WMS_DELIVERY_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "W001", "WMS 전송에 실패했습니다."),

    // 수집
    ORDER_COLLECT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CO001", "주문 수집에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
