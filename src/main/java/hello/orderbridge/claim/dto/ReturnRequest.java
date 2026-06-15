package hello.orderbridge.claim.dto;

public record ReturnRequest(
        Long orderItemId,
        String reason,
        String pickupAddress,
        String carrierCode,
        int refundAmount,
        String refundMethod
) {
}
