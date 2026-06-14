package hello.orderbridge.pipeline;

import hello.orderbridge.config.RabbitMqConfig;
import hello.orderbridge.pipeline.dto.OrderMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProducer {
    private final RabbitTemplate rabbitTemplate;

    public void publish(OrderMessage message) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.ORDERS_EXCHANGE, RabbitMqConfig.ORDERS_ROUTING_KEY, message);
    }
}
