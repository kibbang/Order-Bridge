package hello.orderbridge.order.dto.response;

import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.OrderStatusHistory;

import java.time.LocalDateTime;

public record StatusHistoryResponse(
        OrderStatus fromStatus,
        OrderStatus toStatus,
        String reason,
        LocalDateTime changedAt
) {
    public static StatusHistoryResponse from(OrderStatusHistory history) {
        return new StatusHistoryResponse(
            history.getFromStatus(),
            history.getToStatus(),
            history.getReason(),
            history.getChangedAt()
        );
    }
}
