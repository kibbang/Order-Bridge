package hello.orderbridge.order.repository;

import hello.orderbridge.TestcontainersConfig;
import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.channel.repository.ChannelRepository;
import hello.orderbridge.config.QueryDslConfig;
import hello.orderbridge.enums.channel.ChannelType;
import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.dto.OrderSearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest // JPA 빈만 로드
@Import({TestcontainersConfig.class, QueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ChannelRepository channelRepository;

    private Channel coupang;

    @BeforeEach
    // 쿠팡 주문 하나만 찾기
    void setUp() {
        coupang = channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.COUPANG)
                .findFirst()
                .orElseThrow();
    }

    @Test
    @DisplayName("채널 타입으로 주문 검색")
    void searchByChannelType() {
        // given
        Order order = createOrder("TEST-001", coupang);
        orderRepository.save(order);

        OrderSearchCondition condition = new OrderSearchCondition();
        condition.setChannelType(ChannelType.COUPANG);

        // when
        Page<Order> result = orderRepository.search(condition, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getChannel().getType()).isEqualTo(ChannelType.COUPANG);
    }

    @Test
    @DisplayName("주문 상태로 검색")
    void searchByStatus() {
        // given
        Order order = createOrder("TEST-002", coupang);
        orderRepository.save(order);

        OrderSearchCondition condition = new OrderSearchCondition();
        condition.setStatus(OrderStatus.COLLECTED);

        // when
        Page<Order> result = orderRepository.search(condition, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("채널 주문번호 중복 확인")
    void existsByChannelOrderNo() {
        // given
        Order order = createOrder("DUPLICATE-001", coupang);
        orderRepository.save(order);

        // when & then
        assertThat(orderRepository.existsByChannelOrderNo("DUPLICATE-001")).isTrue();
        assertThat(orderRepository.existsByChannelOrderNo("NONEXIST-001")).isFalse();
    }

    @Test
    @DisplayName("조건 없이 전체 검색")
    void searchAll() {
        // given
        orderRepository.save(createOrder("ALL-001", coupang));
        orderRepository.save(createOrder("ALL-002", coupang));

        OrderSearchCondition condition = new OrderSearchCondition();

        // when
        Page<Order> result = orderRepository.search(condition, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent().size()).isGreaterThanOrEqualTo(2);
    }

    private Order createOrder(String channelOrderNo, Channel channel) {
        return Order.of(
                channelOrderNo,
                channel,
                "주문자",
                "010-1234-5678",
                "수령자",
                "010-8765-4321",
                "서울시 강남구",
                "문앞에 놓아주세요",
                50000,
                LocalDateTime.now()
        );
    }
}
