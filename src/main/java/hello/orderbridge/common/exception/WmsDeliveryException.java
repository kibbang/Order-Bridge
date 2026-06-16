package hello.orderbridge.common.exception;

public class WmsDeliveryException extends BusinessException  {
    public WmsDeliveryException() {
        super(ErrorCode.WMS_DELIVERY_FAILED);
    }
}
