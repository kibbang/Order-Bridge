package hello.orderbridge.collector.dto.external.coupang;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class CoupangOrderResponse {

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private List<CoupangOrder> data;

    @JsonProperty("nextToken")
    private String nextToken;
}
