package hello.orderbridge.order.domain;

import hello.orderbridge.enums.order.WmsStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "wms_deliveries")
public class WmsDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    private String wmsOrderNo;
    private String wmsUrl;

    @Column(nullable = false)
    private int attemptCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WmsStatus status;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    private LocalDateTime sentAt;

    public static WmsDelivery of(Order order) {
        WmsDelivery delivery = new WmsDelivery();
        delivery.order = order;
        delivery.attemptCount = 0;
        delivery.status = WmsStatus.PENDING;
        return delivery;
    }

    public void recordSuccess(String wmsOrderNo, String wmsUrl) {
        this.wmsOrderNo = wmsOrderNo;
        this.wmsUrl = wmsUrl;
        this.status = WmsStatus.SUCCESS;
        this.sentAt = LocalDateTime.now();
        this.attemptCount++;
    }

    public void recordFailure(String errorMessage) {
        this.lastError = errorMessage;
        this.attemptCount++;
        this.status = (this.attemptCount >= 3) ? WmsStatus.EXHAUSTED : WmsStatus.FAILED;
    }
}
