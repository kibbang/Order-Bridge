package hello.orderbridge.order.domain;

import hello.orderbridge.enums.order.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "order_status_histories")
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus toStatus;

    private String reason;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    public static OrderStatusHistory of(
            Order order,
            OrderStatus fromStatus,
            OrderStatus toStatus,
            String reason
    ) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.order = order;
        history.fromStatus = fromStatus;
        history.toStatus = toStatus;
        history.reason = reason;
        history.changedAt = LocalDateTime.now();
        return history;
    }
}
