package hello.orderbridge.pipeline;

import hello.orderbridge.config.RabbitMqConfig;
import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.controller.OrderSseController;
import hello.orderbridge.order.repository.OrderRepository;
import hello.orderbridge.pipeline.dto.OrderMessage;
import hello.orderbridge.wms.WmsService;
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
    private final WmsService wmsService;
    private final OrderSseController orderSseController;

    @Transactional
    @RabbitListener(queues = RabbitMqConfig.ORDERS_QUEUE)
    public void handle(OrderMessage message) {
        orderRepository.findById(message.orderId())
                .ifPresentOrElse(
                        order -> {
                            order.changeStatus(OrderStatus.PROCESSING, "파이프라인 수신");
                            wmsService.deliver(order);
                            orderSseController.sendOrderUpdate(
                                    order.getId(),
                                    order.getStatus().name()
                            );
                        },
                        () -> log.warn("주문을 찾을 수 없음: {}", message.orderId())
                );
    }
}
