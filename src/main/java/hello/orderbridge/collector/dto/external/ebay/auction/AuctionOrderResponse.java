package hello.orderbridge.collector.dto.external.ebay.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionOrderResponse {
    @JsonProperty("ResultCode")
    private int resultCode;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Data")
    private AuctionOrderData data;

}
