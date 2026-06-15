package hello.orderbridge.enums.claim;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClaimStatus {
    REQUESTED("접수"),
    APPROVED("승인"),
    REJECTED("거절"),
    COMPLETED("완료");

    private final String label;
}
