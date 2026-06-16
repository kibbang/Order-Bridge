package hello.orderbridge.common.exception;

public class DuplicateClaimException extends BusinessException  {
    public DuplicateClaimException() {
        super(ErrorCode.CLAIM_ALREADY_EXISTS);
    }
}
