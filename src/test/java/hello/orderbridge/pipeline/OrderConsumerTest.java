package hello.orderbridge.pipeline;

import hello.orderbridge.enums.channel.ChannelType;
import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.repository.OrderRepository;
import hello.orderbridge.pipeline.dto.OrderMessage;
import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.wms.WmsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    WmsService wmsService;

    @InjectMocks
    OrderConsumer orderConsumer;

    @Test
    void 메시지_수신_시_주문_상태를_PROCESSING으로_변경한다() {
        // Given
        Channel channel = Channel.of("쿠팡", ChannelType.COUPANG, "test-key");
        Order order = Order.of(
                "ORD-001", channel,
                "홍길동", "010-1234-5678",
                "김수령", "010-9876-5432",
                "서울시 강남구", "문 앞에",
                30000, LocalDateTime.of(2025, 6, 1, 10, 0)
        );
        OrderMessage message = new OrderMessage(1L, "ORD-001", ChannelType.COUPANG);

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // When
        orderConsumer.handle(message);

        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(order.getStatusHistories()).hasSize(1);
    }
}
