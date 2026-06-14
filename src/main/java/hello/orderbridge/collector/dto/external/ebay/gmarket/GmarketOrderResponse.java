package hello.orderbridge.collector.dto.external.ebay.gmarket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmarketOrderResponse {
    @JsonProperty("ResultCode")
    private int resultCode;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Data")
    private GmarketOrderData data;
}
