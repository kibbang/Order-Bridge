package hello.orderbridge.wms.dto;

public record WmsResponse(
        boolean success,
        String wmsOrderNo,
        String message
) {
}
