package hello.orderbridge.enums.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WmsStatus {
    PENDING("대기"),
    SUCCESS("성공"),
    FAILED("실패"),
    EXHAUSTED("재시도 초과");

    private final String label;
}
