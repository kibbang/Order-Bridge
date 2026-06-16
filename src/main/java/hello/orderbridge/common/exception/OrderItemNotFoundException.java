package hello.orderbridge.common.exception;

public class OrderItemNotFoundException extends BusinessException  {
    public OrderItemNotFoundException() {
        super(ErrorCode.ORDER_ITEM_NOT_FOUND);
    }
}
