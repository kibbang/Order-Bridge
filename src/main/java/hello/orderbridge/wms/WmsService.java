package hello.orderbridge.wms;

import hello.orderbridge.enums.order.OrderStatus;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.domain.WmsDelivery;
import hello.orderbridge.order.repository.OrderRepository;
import hello.orderbridge.wms.dto.WmsRequest;
import hello.orderbridge.wms.dto.WmsResponse;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class WmsService {

    private final WmsClient wmsClient;
    private final OrderRepository orderRepository;
    private final RetryTemplate retryTemplate = new RetryTemplate();
    private final MeterRegistry meterRegistry;

    public WmsService(WmsClient wmsClient, OrderRepository orderRepository, MeterRegistry meterRegistry) {
        this.wmsClient = wmsClient;
        this.orderRepository = orderRepository;
        this.meterRegistry = meterRegistry;

        meterRegistry.counter("wms.delivery.count", "result", "success");
        meterRegistry.counter("wms.delivery.count", "result", "failure");
    }

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

                // 성공 case
                meterRegistry.counter("wms.delivery.count", "result", "success").increment();
            });
        } catch (RuntimeException e) {
            // 재시도 소진 — 실패 처리
            order.changeStatus(OrderStatus.WMS_FAILED, e.getMessage());
            wmsDelivery.recordFailure(e.getMessage());

            // 실패 case
            meterRegistry.counter("wms.delivery.count", "result", "failure").increment();
        }

        order.addWmsDelivery(wmsDelivery);
    }
}
