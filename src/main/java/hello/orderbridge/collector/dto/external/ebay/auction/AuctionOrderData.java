package hello.orderbridge.collector.dto.external.ebay.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionOrderData {
    @JsonProperty("RequestOrders")
    private List<AuctionOrder> requestOrders;
}
