package hello.orderbridge.claim.domain;

import hello.orderbridge.common.domain.BaseEntity;
import hello.orderbridge.enums.claim.ClaimStatus;
import hello.orderbridge.enums.order.ItemStatus;
import hello.orderbridge.order.domain.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static hello.orderbridge.enums.claim.ClaimStatus.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "claim_type")
@Table(name = "claims")
public abstract class Claim extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @Column(nullable = false)
    private String reason;

    protected void init(OrderItem orderItem, String reason) {
        this.orderItem = orderItem;
        this.reason = reason;
        this.status = REQUESTED;
    }

    public abstract ItemStatus getRequestedItemStatus();

    public abstract ItemStatus getApprovedItemStatus();

    public void approve() {
        this.status = APPROVED;
    }

    public void reject()  {
        this.status = REJECTED;
    }

    public void complete() {
        this.status = COMPLETED;
    }
}
