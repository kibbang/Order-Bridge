package hello.orderbridge.collector.dto.external.coupang;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoupangOrderItem {
    // 상품 코드로 사용
    @JsonProperty("vendorItemId")
    private Long vendorItemId;

    // 상품명으로 사용
    @JsonProperty("vendorItemName")
    private String vendorItemName;

    // 수량
    @JsonProperty("shippingCount")
    private Integer shippingCount;

    // 단가
    @JsonProperty("salesPrice")
    private CoupangPrice salesPrice;
}
