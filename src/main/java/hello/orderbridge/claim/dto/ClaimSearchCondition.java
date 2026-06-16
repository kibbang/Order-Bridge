package hello.orderbridge.claim.dto;

import hello.orderbridge.enums.claim.ClaimStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimSearchCondition {
    private ClaimStatus status;
}
