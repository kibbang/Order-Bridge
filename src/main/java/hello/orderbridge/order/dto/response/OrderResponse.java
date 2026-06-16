package hello.orderbridge.order.dto.response;

import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;

import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String channelOrderNo,
        String channelName,      // Channel 엔티티에서 꺼낸 이름
        String ordererName,
        int totalAmount,
        OrderStatus status,
        LocalDateTime orderedAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getChannelOrderNo(),
                order.getChannel().getName(),
                order.getOrdererName(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getOrderedAt()
        );
    }
}
