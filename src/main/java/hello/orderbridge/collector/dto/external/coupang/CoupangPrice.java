package hello.orderbridge.collector.dto.external.coupang;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoupangPrice {

    @JsonProperty("units")
    private Integer units;

    @JsonProperty("nanos")
    private Integer nanos;
}
