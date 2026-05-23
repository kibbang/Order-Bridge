package hello.orderbridge.collector.dto;

public record RawOrderItemDto(
        String productCode,   // 상품 코드
        String productName,   // 상품명
        Integer quantity,      // 수량
        Integer unitPrice     // 단가
) {
}
