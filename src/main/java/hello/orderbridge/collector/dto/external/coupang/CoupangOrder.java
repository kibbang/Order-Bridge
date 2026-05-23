package hello.orderbridge.collector.dto.external.coupang;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoupangOrder {
    @JsonProperty("shipmentBoxId")
    private Long shipmentBoxId; // 배송번호

    // 채널 주문번호 (중복 체크 키)
    @JsonProperty("orderId")
    private Long orderId;

    // 주문 일시 (쿠팡은 offset 포함 형식 ex. 2026-05-23T14:17:13.973885-08:00)
    @JsonProperty("orderedAt")
    private String orderedAt;

    // 주문자 정보
    @JsonProperty("orderer")
    private CoupangOrderer orderer;

    // 수령인 정보
    @JsonProperty("receiver")
    private CoupangReceiver receiver;

    // 배송 메모
    @JsonProperty("parcelPrintMessage")
    private String parcelPrintMessage;

    // 주문 상품 목록
    @JsonProperty("orderItems")
    private List<CoupangOrderItem> orderItems;
}
