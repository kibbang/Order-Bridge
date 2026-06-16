package hello.orderbridge.order.controller.api;

import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.enums.channel.ChannelType;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderApiController.class)
@WithMockUser
class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("주문 목록 조회 API")
    void getOrderList() throws Exception {
        // given
        Channel channel = Channel.of("쿠팡", ChannelType.COUPANG, "key");
        Order order = Order.of("ORD-001", channel, "주문자", "010-1234-5678",
                "수령자", "010-8765-4321", "서울시 강남구", "문앞",
                50000, LocalDateTime.now());

        given(orderService.getOrderList(any(), any()))
                .willReturn(new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1));

        // when & then
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].channelOrderNo").value("ORD-001"));
    }

    @Test
    @DisplayName("주문 상세 조회 API")
    void getOrder() throws Exception {
        // given
        Channel channel = Channel.of("쿠팡", ChannelType.COUPANG, "key");
        Order order = Order.of("ORD-002", channel, "주문자", "010-1234-5678",
                "수령자", "010-8765-4321", "서울시 강남구", "문앞",
                30000, LocalDateTime.now());

        given(orderService.getOrder(1L)).willReturn(order);

        // when & then
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.channelOrderNo").value("ORD-002"));
    }
}
