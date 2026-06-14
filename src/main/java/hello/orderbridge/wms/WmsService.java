package hello.orderbridge.wms;

import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.domain.WmsDelivery;
import hello.orderbridge.order.repository.OrderRepository;
import hello.orderbridge.wms.dto.WmsRequest;
import hello.orderbridge.wms.dto.WmsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class WmsService {

    private final WmsClient wmsClient;
    private final OrderRepository orderRepository;
    private final RetryTemplate retryTemplate = new RetryTemplate();

    public void deliver(Order order) {
        WmsRequest wmsRequest = new WmsRequest(
                order.getChannelOrderNo(),
                order.getOrdererName(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getDeliveryAddress(),
                order.getTotalAmount()
        );

        WmsDelivery wmsDelivery = WmsDelivery.of(order);

        try {
            retryTemplate.invoke(() -> {
                WmsResponse wmsResponse = wmsClient.send(wmsRequest);

                if (!wmsResponse.success()) {
                    throw new RuntimeException(wmsResponse.message());
                }

                // 성공 처리
                order.changeStatus(OrderStatus.WMS_SENT, wmsResponse.wmsOrderNo());
                wmsDelivery.recordSuccess(
                        wmsResponse.wmsOrderNo(),
                        "http://localhost:8080/wms/orders/" + wmsResponse.wmsOrderNo()
                );
            });
        } catch (RuntimeException e) {
            // 재시도 소진 — 실패 처리
            order.changeStatus(OrderStatus.WMS_FAILED, e.getMessage());
            wmsDelivery.recordFailure(e.getMessage());
        }

        order.addWmsDelivery(wmsDelivery);
    }
}
