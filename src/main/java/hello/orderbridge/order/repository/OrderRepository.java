package hello.orderbridge.order.repository;

import hello.orderbridge.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    boolean existsByChannelOrderNo(String channelOrderNo);
}
