package hello.orderbridge.order.controller.api;

import hello.orderbridge.common.dto.ApiResponse;
import hello.orderbridge.common.dto.PageResponse;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.dto.OrderSearchCondition;
import hello.orderbridge.order.dto.response.OrderDetailResponse;
import hello.orderbridge.order.dto.response.OrderResponse;
import hello.orderbridge.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @GetMapping()
    public ApiResponse<PageResponse<OrderResponse>> getOrderList(
            @ModelAttribute OrderSearchCondition condition,
            Pageable pageable
    ) {
        // orderService에서 Page<Order>를 받아서
        // Page<OrderResponse>로 변환 후 PageResponse로 감싸기
        Page<Order> orderList = orderService.getOrderList(condition, pageable);

        Page<OrderResponse> responsePage  = orderList.map(OrderResponse::from);

        return ApiResponse.ok(PageResponse.from(responsePage));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrder(
            @PathVariable Long orderId
    ) {
        Order order = orderService.getOrder(orderId);

        OrderDetailResponse response = OrderDetailResponse.from(order);

        return ApiResponse.ok(response);
    }
}