package hello.orderbridge.order.service;

import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * 주문 목록 조회
     */
    public List<Order> getOrderList() {
        return orderRepository.findAll();
    }

    /**
     * 주문 상세 조회
     * @param id
     */
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new RuntimeException("주문을 찾을 수 없습니다.")
        );
    }
}
