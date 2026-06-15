package hello.orderbridge.claim.dto;

public record CancelRequest(
        Long orderItemId,
        String reason,
        int refundAmount,
        String refundMethod
) {
}
