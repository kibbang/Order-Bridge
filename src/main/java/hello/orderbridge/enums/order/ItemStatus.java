package hello.orderbridge.enums.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemStatus {
    NORMAL("정상"),

    CANCEL_REQUESTED("취소 신청"),
    CANCEL_COMPLETED("취소 완료"),

    RETURN_REQUESTED("반품 신청"),
    RETURN_COMPLETED("반품 완료"),

    EXCHANGE_REQUESTED("교환 신청"),
    EXCHANGE_COMPLETED("교환 완료");

    private final String label;
}
