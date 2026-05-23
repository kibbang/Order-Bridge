package hello.orderbridge.collector.dto.external.coupang;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoupangReceiver {
    @JsonProperty("name")
    private String name;

    @JsonProperty("safeNumber")
    private String safeNumber;

    @JsonProperty("addr1")
    private String addr1;

    @JsonProperty("addr2")
    private String addr2;
}
