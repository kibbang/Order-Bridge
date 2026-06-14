package hello.orderbridge.pipeline;

import hello.orderbridge.config.RabbitMqConfig;
import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.repository.OrderRepository;
import hello.orderbridge.pipeline.dto.OrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final OrderRepository orderRepository;

    @Transactional
    @RabbitListener(queues = RabbitMqConfig.ORDERS_QUEUE)
    public void handle(OrderMessage message) {
        orderRepository.findById(message.orderId())
                .ifPresentOrElse(
                        order -> order.changeStatus(OrderStatus.PROCESSING, "파이프라인 수신"),
                        () -> log.warn("주문을 찾을 수 없음: {}", message.orderId())
                );
    }
}
