package hello.orderbridge.order.repository;

import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.dto.OrderSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    Page<Order> search(OrderSearchCondition condition, Pageable pageable);
}
