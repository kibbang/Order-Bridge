package hello.orderbridge.claim.domain;

import hello.orderbridge.enums.claim.RefundMethod;
import hello.orderbridge.enums.order.ItemStatus;
import hello.orderbridge.order.domain.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("CANCEL")
@Table(name = "cancels")
public class Cancel extends Claim {
    @Column(nullable = false)
    private int refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundMethod refundMethod;

    public static Cancel of(
            OrderItem orderItem,
            String reason,
            int refundAmount,
            RefundMethod refundMethod) {
        Cancel cancel = new Cancel();
        cancel.init(orderItem, reason);
        cancel.refundAmount = refundAmount;
        cancel.refundMethod = refundMethod;
        return cancel;
    }

    @Override
    public ItemStatus getRequestedItemStatus() {
        return ItemStatus.CANCEL_REQUESTED;
    }

    @Override
    public ItemStatus getApprovedItemStatus() {
        return ItemStatus.CANCEL_COMPLETED;
    }
}
