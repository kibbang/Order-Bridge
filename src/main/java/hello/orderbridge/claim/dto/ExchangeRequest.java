package hello.orderbridge.claim.dto;

public record ExchangeRequest(
        Long orderItemId,
        String reason,
        String exchangeProductCode,
        String deliveryAddress,
        String carrierCode
) {
}
