package hello.orderbridge.order.service;

import hello.orderbridge.common.exception.OrderNotFoundException;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.dto.OrderSearchCondition;
import hello.orderbridge.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * 주문 목록 조회
     */
    public Page<Order> getOrderList(OrderSearchCondition condition, Pageable pageable) {
        return orderRepository.search(condition, pageable);
    }

    /**
     * 주문 상세 조회
     * @param id
     */
    @Cacheable(value = "order", key = "#id")
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(
                OrderNotFoundException::new
        );
    }
}
