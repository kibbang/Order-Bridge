package hello.orderbridge.order.domain;

import hello.orderbridge.common.domain.BaseEntity;
import hello.orderbridge.enums.order.ItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "order_items")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String sellerCode;

    @Column(nullable = false)
    private int itemSeq;

    @Column(nullable = false)
    private int unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus itemStatus;

    public static OrderItem of(
            Order order,
            String productName,
            String productCode,
            String sellerCode,
            int itemSeq,
            int unitPrice
    ) {
        OrderItem item = new OrderItem();
        item.order = order;
        item.productName = productName;
        item.productCode = productCode;
        item.sellerCode = sellerCode;
        item.itemSeq = itemSeq;
        item.unitPrice = unitPrice;
        item.itemStatus = ItemStatus.NORMAL;
        return item;
    }

    public void changeStatus(ItemStatus newStatus) {
        this.itemStatus = newStatus;
    }
}
