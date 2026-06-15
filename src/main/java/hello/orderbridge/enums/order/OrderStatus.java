package hello.orderbridge.enums.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    COLLECTED("수집 완료"),
    PROCESSING("처리중"),
    WMS_SENT("WMS 전송"),
    WMS_FAILED("WMS 실패"),
    COMPLETED("완료");

    private final String label;
}
