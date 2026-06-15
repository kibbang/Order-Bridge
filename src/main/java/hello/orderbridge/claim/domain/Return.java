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
@DiscriminatorValue("RETURN")
@Table(name = "returns")
public class Return extends Claim {
    @Column(nullable = false)
    private String pickupAddress;

    @Column(nullable = false)
    private String carrierCode;

    private String trackingNo;

    @Column(nullable = false)
    private int refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundMethod refundMethod;

    public static Return of(
            OrderItem orderItem, String reason,
            String pickupAddress, String carrierCode,
            int refundAmount, RefundMethod refundMethod) {
        Return claim = new Return();
        claim.init(orderItem, reason);
        claim.pickupAddress = pickupAddress;
        claim.carrierCode = carrierCode;
        claim.refundAmount = refundAmount;
        claim.refundMethod = refundMethod;
        return claim;
    }

    public void registerTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    @Override
    public ItemStatus getRequestedItemStatus() {
        return ItemStatus.RETURN_REQUESTED;
    }

    @Override
    public ItemStatus getApprovedItemStatus() {
        return ItemStatus.RETURN_COMPLETED;
    }
}
