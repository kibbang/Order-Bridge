package hello.orderbridge.common.exception;

public class OrderCollectException extends BusinessException  {
    public OrderCollectException() {
        super(ErrorCode.ORDER_COLLECT_FAILED);

    }
}
