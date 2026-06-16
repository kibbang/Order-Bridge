package hello.orderbridge.order.dto.response;

import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long id,
        String channelOrderNo,
        String channelName,
        String ordererName,
        String ordererPhone,
        String receiverName,
        String receiverPhone,
        String deliveryAddress,
        String deliveryMemo,
        int totalAmount,
        OrderStatus status,
        LocalDateTime orderedAt,
        List<OrderItemResponse> items,
        List<StatusHistoryResponse> statusHistories
) {
    public static OrderDetailResponse from(Order order) {
        return  new OrderDetailResponse(
                order.getId(),
                order.getChannelOrderNo(),
                order.getChannel().getName(),
                order.getOrdererName(),
                order.getOrdererPhone(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getDeliveryAddress(),
                order.getDeliveryMemo(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getOrderedAt(),
                getItemResponse(order),
                getStatusHistories(order)
        );
    }

    private static List<OrderItemResponse> getItemResponse(Order order) {
        return order.getItems()
                .stream()
                .map(OrderItemResponse::from)
                .toList();
    }

    private static List<StatusHistoryResponse> getStatusHistories(Order order) {
        return order.getStatusHistories()
                .stream()
                .map(StatusHistoryResponse::from)
                .toList();
    }
}
