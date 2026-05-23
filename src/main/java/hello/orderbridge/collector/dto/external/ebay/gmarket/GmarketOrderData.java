package hello.orderbridge.collector.dto.external.ebay.gmarket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmarketOrderData {
    @JsonProperty("RequestOrders")
    private List<GmarketOrder> requestOrders;
}
