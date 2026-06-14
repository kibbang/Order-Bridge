package hello.orderbridge.collector;

import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.collector.dto.RawOrderDto;
import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.domain.OrderItem;
import hello.orderbridge.order.repository.OrderRepository;
import hello.orderbridge.pipeline.OrderProducer;
import hello.orderbridge.pipeline.dto.OrderMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderCollectService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    @Transactional
    public void saveOrders(Channel channel, List<RawOrderDto> rawOrders) {
        /*
        rawOrders를 순회하면서:
            1. 중복 체크 — orderRepository.existsByChannelOrderNo()로 이미 있으면 skip
            2. Order 생성 — Order.of()에 channel + RawOrderDto 필드들 매핑
                ※ channelOrderNo는 Long → String 변환 필요 (Order 엔티티가 String 타입)
            3. OrderItem 생성 — RawOrderItemDto를 순회하면서 OrderItem.of() 호출
                ※ 수량 분할: quantity가 3이면 OrderItem 3개 (수량 고정 1, itemSeq = 1,2,3)
                ※ sellerCode는 지금 mock 데이터에 없으니 productCode로 대체해도 OK
            4. order.getItems().add(item)으로 양방향 연관관계 설정
            5. orderRepository.save(order)
         */

        rawOrders.forEach(rawOrder -> {
            boolean orderExists = orderRepository.existsByChannelOrderNo(rawOrder.channelOrderNo());

            if (orderExists) {
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
    }
}
