package hello.orderbridge.order.controller;

import hello.orderbridge.enums.channel.ChannelType;
import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.dto.OrderSearchCondition;
import hello.orderbridge.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping()
    public String getOrderList(
            @ModelAttribute OrderSearchCondition condition,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        Page<Order> orderList = orderService.getOrderList(condition, PageRequest.of(page , size));

        model.addAttribute("orders", orderList.getContent());
        model.addAttribute("condition", condition);
        model.addAttribute("page", orderList);
        model.addAttribute("channelTypes", ChannelType.values());
        model.addAttribute("orderStatuses", OrderStatus.values());
        
        return "order/list";
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable Long id, Model model) {
        Order order = orderService.getOrder(id);

        model.addAttribute("order", order);

        return "order/detail";
    }
}
