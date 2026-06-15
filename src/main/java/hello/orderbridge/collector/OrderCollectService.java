package hello.orderbridge.collector;

import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.collector.dto.RawOrderDto;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.domain.OrderItem;
import hello.orderbridge.order.repository.OrderRepository;
import hello.orderbridge.pipeline.OrderProducer;
import hello.orderbridge.pipeline.dto.OrderMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hello.orderbridge.config.RedisConfig.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderCollectService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public void saveOrders(Channel channel, List<RawOrderDto> rawOrders) {
        String redisKey = COLLECTED_KEY_PREFIX + channel.getType().name();
        rawOrders.forEach(rawOrder -> {
            boolean isNewOrder = redisTemplate
                    .opsForSet()
                    .add(redisKey, rawOrder.channelOrderNo()) > 0;

            if (!isNewOrder) {
                return;
            }

            Order order = Order.of(
                    rawOrder.channelOrderNo(),
                    channel,
                    rawOrder.ordererName(),
                    rawOrder.ordererPhone(),
                    rawOrder.receiverName(),
                    rawOrder.receiverPhone(),
                    rawOrder.deliveryAddress(),
                    rawOrder.deliveryMemo(),
                    rawOrder.totalAmount(),
                    rawOrder.orderedAt()
            );

            rawOrder.items().forEach(item -> {

                for (int i = 1; i <= item.quantity(); i++) {
                    OrderItem orderItem = OrderItem.of(
                            item.productName(),
                            item.productCode(),
                            item.sellerCode(),
                            i,
                            item.unitPrice()
                    );

                    order.addOrderItem(orderItem);
                }
            });

            orderRepository.save(order);

            OrderMessage orderMessage = new OrderMessage(
                    order.getId(),
                    order.getChannelOrderNo(),
                    channel.getType()
            );

            orderProducer.publish(orderMessage);
        });

        redisTemplate.delete(CACHE_ORDERS_ALL_KEY);
    }
}
