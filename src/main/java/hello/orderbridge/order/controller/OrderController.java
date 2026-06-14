package hello.orderbridge.order.controller;

import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping()
    public String getOrderList(Model model) {
        List<Order> orderList = orderService.getOrderList();

        model.addAttribute("orders", orderList);

        return "order/list";
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable Long id, Model model) {
        Order order = orderService.getOrder(id);

        model.addAttribute("order", order);

        return "order/detail";
    }
}
