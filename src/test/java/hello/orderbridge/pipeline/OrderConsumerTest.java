package hello.orderbridge.pipeline;

import hello.orderbridge.enums.channel.ChannelType;
import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.controller.OrderSseController;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.repository.OrderRepository;
import hello.orderbridge.pipeline.dto.OrderMessage;
import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.wms.WmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static hello.orderbridge.config.RedisConfig.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    WmsService wmsService;

    @Mock
    OrderSseController orderSseController;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    ValueOperations<String, Object> valueOperations;

    @InjectMocks
    OrderConsumer orderConsumer;

    Channel channel;
    Order order;
    OrderMessage message;

    @BeforeEach
    void setUp() {
        channel = Channel.of("쿠팡", ChannelType.COUPANG, "test-key");
        order = Order.of(
                "ORD-001", channel,
                "홍길동", "010-1234-5678",
                "김수령", "010-9876-5432",
                "서울시 강남구", "문 앞에",
                30000, LocalDateTime.of(2025, 6, 1, 10, 0)
        );
        message = new OrderMessage(1L, "ORD-001", ChannelType.COUPANG);
    }

    @Test
    void 메시지_수신_시_주문_상태를_PROCESSING으로_변경한다() {
        // Given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(
                eq(CONSUMED_KEY_PREFIX + 1L), eq("1"), any(Duration.class))
        ).willReturn(true);
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // When
        orderConsumer.handle(message);

        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(order.getStatusHistories()).hasSize(1);
    }

    @Test
    void 중복_메시지는_처리하지_않는다() {
        // Given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(
                eq(CONSUMED_KEY_PREFIX + 1L), eq("1"), any(Duration.class))
        ).willReturn(false);

        // When
        orderConsumer.handle(message);

        // Then
        verify(orderRepository, never()).findById(any());
        verify(wmsService, never()).deliver(any());
    }

    @Test
    void 메시지_처리_후_WMS에_전달한다() {
        // Given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(
                eq(CONSUMED_KEY_PREFIX + 1L), eq("1"), any(Duration.class))
        ).willReturn(true);
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // When
        orderConsumer.handle(message);

        // Then
        verify(wmsService).deliver(order);
    }

    @Test
    void 메시지_처리_후_캐시를_무효화한다() {
        // Given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(
                eq(CONSUMED_KEY_PREFIX + 1L), eq("1"), any(Duration.class))
        ).willReturn(true);
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // When
        orderConsumer.handle(message);

        // Then — order.getId()는 DB 저장 전이라 null
        verify(redisTemplate).delete(CACHE_ORDER + "::" + order.getId());
        verify(redisTemplate).delete(CACHE_ORDERS_ALL_KEY);
    }

    @Test
    void 메시지_처리_후_SSE_이벤트를_발행한다() {
        // Given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(
                eq(CONSUMED_KEY_PREFIX + 1L), eq("1"), any(Duration.class))
        ).willReturn(true);
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // When
        orderConsumer.handle(message);

        // Then
        verify(orderSseController).sendOrderUpdate(any(), eq("PROCESSING"));
    }
}
