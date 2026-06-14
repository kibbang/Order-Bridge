package hello.orderbridge.collector.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.collector.ChannelCollector;
import hello.orderbridge.collector.dto.RawOrderDto;
import hello.orderbridge.collector.dto.RawOrderItemDto;
import hello.orderbridge.collector.dto.external.coupang.*;
import hello.orderbridge.enums.channel.ChannelType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CoupangCollector implements ChannelCollector {

    private final ObjectMapper objectMapper;

    @Override
    public ChannelType getChannelType() {
        return ChannelType.COUPANG;
    }

    @Override
    public List<RawOrderDto> collect(Channel channel) {

        try {
            // Mock up 파일 읽기
            ClassPathResource resource = new ClassPathResource("mock/coupang_orders.json");
            CoupangOrderResponse response = objectMapper.readValue(resource.getInputStream(), CoupangOrderResponse.class);

            return response.getData().stream().map(this::toRawOrderDto).toList();

        } catch (IOException e) {
            throw new RuntimeException("쿠팡 목업 파일 리딩 실패", e);
        }
    }

    private RawOrderDto toRawOrderDto(CoupangOrder order) {
        // 1. deliveryAddress = addr1 + " " + addr2
        // 2. totalAmount = orderItems의 salesPrice.units 합산
        // 3. orderedAt은 OffsetDateTime → toLocalDateTime() 변환
        // 4. items는 stream으로 CoupangOrderItem → RawOrderItemDto 변환

        CoupangReceiver receiver = order.getReceiver();
        String deliveryAddress = receiver.getAddr1() + " " + receiver.getAddr2();

        List<CoupangOrderItem> orderItems = order.getOrderItems();

        int totalAmount = orderItems
                .stream()
                .mapToInt(item -> item.getSalesPrice().getUnits())
                .sum();

        LocalDateTime orderedAt = OffsetDateTime.parse(order.getOrderedAt()).toLocalDateTime();

        List<RawOrderItemDto> rawOrderItems = orderItems.stream().map(this::toRawOrderItemDto).toList();

        CoupangOrderer orderer = order.getOrderer();

        return new RawOrderDto(
                String.valueOf(order.getOrderId()),
                orderer.getName(),
                orderer.getSafeNumber(),
                receiver.getName(),
                receiver.getSafeNumber(),
                deliveryAddress,
                order.getParcelPrintMessage(),
                totalAmount,
                orderedAt,
                rawOrderItems
        );
    }

    private RawOrderItemDto toRawOrderItemDto(CoupangOrderItem item) {
        // productCode = vendorItemId (Long → String 변환)
        String productCode = String.valueOf(item.getVendorItemId());

        return new RawOrderItemDto(
                productCode,
                item.getVendorItemName(),
                productCode,
                item.getShippingCount(),
                item.getSalesPrice().getUnits());
    }
}