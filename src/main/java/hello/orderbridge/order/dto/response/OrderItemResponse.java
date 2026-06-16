package hello.orderbridge.order.dto.response;

import hello.orderbridge.enums.order.ItemStatus;
import hello.orderbridge.order.domain.OrderItem;

public record OrderItemResponse(
        Long id,
        String productName,
        String productCode,
        int itemSeq,
        int unitPrice,
        ItemStatus itemStatus
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductName(),
                item.getProductCode(),
                item.getItemSeq(),
                item.getUnitPrice(),
                item.getItemStatus()
        );
    }
}
