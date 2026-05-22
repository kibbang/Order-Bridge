package hello.orderbridge.claim.domain;

import hello.orderbridge.order.domain.OrderItem;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("EXCHANGE")
@Table(name = "exchanges")
public class Exchange extends Claim {
    @Column(nullable = false)
    private String exchangeProductCode;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String carrierCode;

    private String trackingNo;

    public static Exchange of(
            OrderItem orderItem, String reason,
            String exchangeProductCode, String deliveryAddress,
            String carrierCode
    ) {
        Exchange claim = new Exchange();
        claim.init(orderItem, reason);
        claim.exchangeProductCode = exchangeProductCode;
        claim.deliveryAddress = deliveryAddress;
        claim.carrierCode = carrierCode;
        return claim;
    }

    public void registerTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }
}
