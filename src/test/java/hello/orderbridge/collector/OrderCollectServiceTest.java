package hello.orderbridge.collector;

import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.collector.dto.RawOrderDto;
import hello.orderbridge.collector.dto.RawOrderItemDto;
import hello.orderbridge.enums.channel.ChannelType;
import hello.orderbridge.enums.order.ItemStatus;
import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.repository.OrderRepository;
import hello.orderbridge.pipeline.OrderProducer;
import hello.orderbridge.pipeline.dto.OrderMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.time.LocalDateTime;
import java.util.List;

import static hello.orderbridge.config.RedisConfig.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCollectServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderProducer orderProducer;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    SetOperations<String, Object> setOperations;

    @InjectMocks
    OrderCollectService orderCollectService;

    Channel channel;
    String redisKey;

    @BeforeEach
    void setUp() {
        channel = Channel.of("쿠팡", ChannelType.COUPANG, "test-api-key");
        redisKey = COLLECTED_KEY_PREFIX + ChannelType.COUPANG.name();
        given(redisTemplate.opsForSet()).willReturn(setOperations);
    }

    @Test
    void 정상_주문을_저장한다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-001", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 1, 10000)
        ));
        given(setOperations.add(eq(redisKey), eq("ORD-001"))).willReturn(1L);

        // When
        orderCollectService.saveOrders(channel, List.of(rawOrder));

        // Then
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        Order savedOrder = captor.getValue();
        assertThat(savedOrder.getChannelOrderNo()).isEqualTo("ORD-001");
        assertThat(savedOrder.getOrdererName()).isEqualTo("홍길동");
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.COLLECTED);
        assertThat(savedOrder.getItems()).hasSize(1);
    }

    @Test
    void Redis_Set으로_중복_주문을_필터링한다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-DUP", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 1, 10000)
        ));
        given(setOperations.add(eq(redisKey), eq("ORD-DUP"))).willReturn(0L);

        // When
        orderCollectService.saveOrders(channel, List.of(rawOrder));

        // Then
        verify(orderRepository, never()).save(any());
        verify(orderProducer, never()).publish(any());
    }

    @Test
    void 수량이_여러개면_아이템을_분할_생성한다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-002", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 3, 5000)
        ));
        given(setOperations.add(eq(redisKey), eq("ORD-002"))).willReturn(1L);

        // When
        orderCollectService.saveOrders(channel, List.of(rawOrder));

        // Then
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        Order savedOrder = captor.getValue();
        assertThat(savedOrder.getItems()).hasSize(3);
        assertThat(savedOrder.getItems().get(0).getItemSeq()).isEqualTo(1);
        assertThat(savedOrder.getItems().get(1).getItemSeq()).isEqualTo(2);
        assertThat(savedOrder.getItems().get(2).getItemSeq()).isEqualTo(3);
    }

    @Test
    void 아이템의_초기_상태는_NORMAL이다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-003", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 1, 10000)
        ));
        given(setOperations.add(eq(redisKey), eq("ORD-003"))).willReturn(1L);

        // When
        orderCollectService.saveOrders(channel, List.of(rawOrder));

        // Then
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        assertThat(captor.getValue().getItems().get(0).getItemStatus()).isEqualTo(ItemStatus.NORMAL);
    }

    @Test
    void 여러_주문_중_중복만_필터링한다() {
        // Given
        RawOrderDto newOrder = createRawOrder("ORD-NEW", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 1, 10000)
        ));
        RawOrderDto dupOrder = createRawOrder("ORD-DUP", List.of(
                new RawOrderItemDto("P002", "상품B", "S002", 1, 20000)
        ));
        given(setOperations.add(eq(redisKey), eq("ORD-NEW"))).willReturn(1L);
        given(setOperations.add(eq(redisKey), eq("ORD-DUP"))).willReturn(0L);

        // When
        orderCollectService.saveOrders(channel, List.of(newOrder, dupOrder));

        // Then
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void 여러_상품이_포함된_주문을_저장한다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-004", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 1, 10000),
                new RawOrderItemDto("P002", "상품B", "S002", 2, 5000)
        ));
        given(setOperations.add(eq(redisKey), eq("ORD-004"))).willReturn(1L);

        // When
        orderCollectService.saveOrders(channel, List.of(rawOrder));

        // Then
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        Order savedOrder = captor.getValue();
        assertThat(savedOrder.getItems()).hasSize(3); // 1 + 2
    }

    @Test
    void 주문_저장_후_RabbitMQ로_메시지를_발행한다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-005", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 1, 10000)
        ));
        given(setOperations.add(eq(redisKey), eq("ORD-005"))).willReturn(1L);

        // When
        orderCollectService.saveOrders(channel, List.of(rawOrder));

        // Then
        ArgumentCaptor<OrderMessage> captor = ArgumentCaptor.forClass(OrderMessage.class);
        verify(orderProducer).publish(captor.capture());

        OrderMessage message = captor.getValue();
        assertThat(message.channelOrderNo()).isEqualTo("ORD-005");
        assertThat(message.channelType()).isEqualTo(ChannelType.COUPANG);
    }

    @Test
    void 중복_주문은_메시지를_발행하지_않는다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-DUP2", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 1, 10000)
        ));
        given(setOperations.add(eq(redisKey), eq("ORD-DUP2"))).willReturn(0L);

        // When
        orderCollectService.saveOrders(channel, List.of(rawOrder));

        // Then
        verify(orderProducer, never()).publish(any());
    }

    @Test
    void 주문_저장_후_목록_캐시를_삭제한다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-006", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 1, 10000)
        ));
        given(setOperations.add(eq(redisKey), eq("ORD-006"))).willReturn(1L);

        // When
        orderCollectService.saveOrders(channel, List.of(rawOrder));

        // Then
        verify(redisTemplate).delete(CACHE_ORDERS_ALL_KEY);
    }

    private RawOrderDto createRawOrder(String channelOrderNo, List<RawOrderItemDto> items) {
        return new RawOrderDto(
                channelOrderNo,
                "홍길동",
                "010-1234-5678",
                "김수령",
                "010-9876-5432",
                "서울시 강남구",
                "부재시 문 앞에",
                30000,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                items
        );
    }
}
