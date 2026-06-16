package hello.orderbridge.common.exception;

public class ClaimNotFoundException extends BusinessException  {
    public ClaimNotFoundException() {
        super(ErrorCode.CLAIM_NOT_FOUND);
    }
}
