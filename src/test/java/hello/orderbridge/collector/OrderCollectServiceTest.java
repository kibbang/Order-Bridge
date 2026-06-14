package hello.orderbridge.collector;

import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.collector.dto.RawOrderDto;
import hello.orderbridge.collector.dto.RawOrderItemDto;
import hello.orderbridge.enums.channel.ChannelType;
import hello.orderbridge.enums.order.ItemStatus;
import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCollectServiceTest {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderCollectService orderCollectService;

    Channel channel;

    @BeforeEach
    void setUp() {
        channel = Channel.of("쿠팡", ChannelType.COUPANG, "test-api-key");
    }

    @Test
    void 정상_주문을_저장한다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-001", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 1, 10000)
        ));
        given(orderRepository.existsByChannelOrderNo("ORD-001")).willReturn(false);

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
    void 중복_주문은_저장하지_않는다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-DUP", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 1, 10000)
        ));
        given(orderRepository.existsByChannelOrderNo("ORD-DUP")).willReturn(true);

        // When
        orderCollectService.saveOrders(channel, List.of(rawOrder));

        // Then
        verify(orderRepository, never()).save(any());
    }

    @Test
    void 수량이_여러개면_아이템을_분할_생성한다() {
        // Given
        RawOrderDto rawOrder = createRawOrder("ORD-002", List.of(
                new RawOrderItemDto("P001", "상품A", "S001", 3, 5000)
        ));
        given(orderRepository.existsByChannelOrderNo("ORD-002")).willReturn(false);

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
        given(orderRepository.existsByChannelOrderNo("ORD-003")).willReturn(false);

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
        given(orderRepository.existsByChannelOrderNo("ORD-NEW")).willReturn(false);
        given(orderRepository.existsByChannelOrderNo("ORD-DUP")).willReturn(true);

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
        given(orderRepository.existsByChannelOrderNo("ORD-004")).willReturn(false);

        // When
        orderCollectService.saveOrders(channel, List.of(rawOrder));

        // Then
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        Order savedOrder = captor.getValue();
        assertThat(savedOrder.getItems()).hasSize(3); // 1 + 2
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
