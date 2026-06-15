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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static hello.orderbridge.config.RedisConfig.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final OrderRepository orderRepository;
    private final WmsService wmsService;
    private final OrderSseController orderSseController;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @RabbitListener(queues = RabbitMqConfig.ORDERS_QUEUE)
    public void handle(OrderMessage message) {

        String redisKey = CONSUMED_KEY_PREFIX + message.orderId();
        Boolean ifFirst = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", Duration.ofHours(24));

        if (Boolean.FALSE.equals(ifFirst)) {
            log.info("중복 메시지 무시: orderId={}", message.orderId());
            return;
        }

        orderRepository.findById(message.orderId())
                .ifPresentOrElse(
                        order -> {
                            order.changeStatus(OrderStatus.PROCESSING, "파이프라인 수신");

                            wmsService.deliver(order);
                            evictOrderCache(order.getId());

                            orderSseController.sendOrderUpdate(
                                    order.getId(),
                                    order.getStatus().name()
                            );
                        },
                        () -> log.warn("주문을 찾을 수 없음: {}", message.orderId())
                );
    }

    public void evictOrderCache(Long orderId) {
        redisTemplate.delete(CACHE_ORDER + "::" + orderId); // 상세 캐시
        redisTemplate.delete(CACHE_ORDERS_ALL_KEY); // 목록 캐시
    }
}