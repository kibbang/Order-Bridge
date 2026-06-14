package hello.orderbridge.wms.dto;

public record WmsRequest(
        String channelOrderNo,
        String ordererName,
        String receiverName,
        String receiverPhone,
        String deliveryAddress,
        int totalAmount
) {
}
