package hello.orderbridge.order.domain;

import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.common.domain.BaseEntity;
import hello.orderbridge.enums.order.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String channelOrderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(nullable = false)
    private String ordererName;

    @Column(nullable = false)
    private String ordererPhone;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String receiverPhone;

    @Column(nullable = false)
    private String deliveryAddress;

    private String deliveryMemo;

    @Column(nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderStatusHistory> statusHistories = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private WmsDelivery wmsDelivery;

    public static Order of(
            String channelOrderNo, Channel channel,
            String ordererName, String ordererPhone,
            String receiverName, String receiverPhone,
            String deliveryAddress, String deliveryMemo,
            int totalAmount, LocalDateTime orderedAt) {
        Order order = new Order();
        order.channelOrderNo = channelOrderNo;
        order.channel = channel;
        order.ordererName = ordererName;
        order.ordererPhone = ordererPhone;
        order.receiverName = receiverName;
        order.receiverPhone = receiverPhone;
        order.deliveryAddress = deliveryAddress;
        order.deliveryMemo = deliveryMemo;
        order.totalAmount = totalAmount;
        order.orderedAt = orderedAt;
        order.status = OrderStatus.COLLECTED;
        return order;
    }

    public void changeStatus(OrderStatus newStatus, String reason) {
        OrderStatusHistory history = OrderStatusHistory.of(this, this.status, newStatus, reason);
        this.statusHistories.add(history);
        this.status = newStatus;
    }

    /**
     * 연관관계 편의 메소드
     */
    public void addOrderItem(OrderItem item) {
        this.items.add(item);
        item.assignOrder(this);
    }

    public void addWmsDelivery(WmsDelivery wmsDelivery) {
        this.wmsDelivery = wmsDelivery;
    }
}
