package hello.orderbridge.wms;

import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.enums.channel.ChannelType;
import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.repository.OrderRepository;
import hello.orderbridge.wms.dto.WmsRequest;
import hello.orderbridge.wms.dto.WmsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WmsServiceTest {

    @Mock
    WmsClient wmsClient;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    WmsService wmsService;

    Order order;

    @BeforeEach
    void setUp() {
        Channel channel = Channel.of("쿠팡", ChannelType.COUPANG, "test-key");
        order = Order.of(
                "ORD-001", channel,
                "홍길동", "010-1234-5678",
                "김수령", "010-9876-5432",
                "서울시 강남구", "문 앞에",
                30000, LocalDateTime.of(2025, 6, 1, 10, 0)
        );
    }

    @Test
    void WMS_전송_성공_시_상태가_WMS_SENT로_변경된다() {
        // Given
        given(wmsClient.send(any(WmsRequest.class)))
                .willReturn(new WmsResponse(true, "WMS-12345", null));

        // When
        wmsService.deliver(order);

        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WMS_SENT);
        assertThat(order.getWmsDelivery()).isNotNull();
        assertThat(order.getWmsDelivery().getWmsOrderNo()).isEqualTo("WMS-12345");
    }

    @Test
    void WMS_전송_실패_후_재시도_소진_시_WMS_FAILED로_변경된다() {
        // Given
        given(wmsClient.send(any(WmsRequest.class)))
                .willReturn(new WmsResponse(false, null, "WMS 서버 응답 없음"));

        // When
        wmsService.deliver(order);

        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WMS_FAILED);
        assertThat(order.getWmsDelivery()).isNotNull();
        assertThat(order.getWmsDelivery().getLastError()).isEqualTo("WMS 서버 응답 없음");
    }

    @Test
    void WMS_전송_실패_후_재시도에서_성공하면_WMS_SENT가_된다() {
        // Given
        given(wmsClient.send(any(WmsRequest.class)))
                .willReturn(new WmsResponse(false, null, "WMS 서버 응답 없음"))
                .willReturn(new WmsResponse(true, "WMS-99999", null));

        // When
        wmsService.deliver(order);

        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WMS_SENT);
        assertThat(order.getWmsDelivery().getWmsOrderNo()).isEqualTo("WMS-99999");
    }
}
