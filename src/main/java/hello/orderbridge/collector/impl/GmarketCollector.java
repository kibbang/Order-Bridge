package hello.orderbridge.collector.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.collector.ChannelCollector;
import hello.orderbridge.collector.dto.RawOrderDto;
import hello.orderbridge.collector.dto.RawOrderItemDto;
import hello.orderbridge.collector.dto.external.ebay.gmarket.GmarketOrder;
import hello.orderbridge.collector.dto.external.ebay.gmarket.GmarketOrderData;
import hello.orderbridge.collector.dto.external.ebay.gmarket.GmarketOrderResponse;
import hello.orderbridge.enums.channel.ChannelType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GmarketCollector implements ChannelCollector {

    private final ObjectMapper objectMapper;

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GMARKET;
    }

    @Override
    public List<RawOrderDto> collect(Channel channel) {
        try {
            // Mock up 파일 읽기
            ClassPathResource resource = new ClassPathResource("mock/ebay/gmarket_orders.json");
            GmarketOrderResponse response = objectMapper.readValue(resource.getInputStream(), GmarketOrderResponse.class);

            GmarketOrderData data = response.getData();

            return data.getRequestOrders().stream().map(this::toRawOrderDto).toList();

        } catch (IOException e) {
            throw new RuntimeException("G마켓 목업 파일 리딩 실패", e);
        }
    }

    private RawOrderDto toRawOrderDto(GmarketOrder order) {
        // 1. deliveryAddress = DelFullAddress 그대로 사용 (addr1+addr2 합칠 필요 없음)
        // 2. totalAmount = OrderAmount를 String → int 변환 (Double.parseDouble() 활용)
        // 3. orderedAt = OrderDate가 LocalDateTime 형식이라 바로 사용 가능
        // 4. items = 주문 1건에 상품 1개라 List.of()로 감싸서 반환
        //    └── productCode = SiteGoodsNo
        //    └── productName = GoodsName
        //    └── quantity = ContrAmount
        //    └── unitPrice = SalePrice를 String → int 변환
        // 5. channelOrderNo = OrderNo (Long 타입)
        // 6. ordererPhone = BuyerMobileTel
        // 7. receiverPhone = HpNo

        String deliveryAddress = order.getDelFullAddress();
        String ordererName = order.getBuyerName();
        int totalAmount = (int) Double.parseDouble(order.getOrderAmount());
        LocalDateTime orderedAt = order.getOrderDate();
        List<RawOrderItemDto> items = List.of(toRawOrderItemDto(order));
        Long channelOrderNo = order.getOrderNo();
        String ordererPhone = order.getBuyerMobileTel();
        String receiverName = order.getReceiverName();
        String receiverPhone = order.getHpNo();
        String deliveryMemo = order.getDelMemo();

        return new RawOrderDto(
                channelOrderNo,
                ordererName,
                ordererPhone,
                receiverName,
                receiverPhone,
                deliveryAddress,
                deliveryMemo,
                totalAmount,
                orderedAt,
                items
        );
    }

    private RawOrderItemDto toRawOrderItemDto(GmarketOrder order) {
        return new RawOrderItemDto(
                order.getSiteGoodsNo(),
                order.getGoodsName(),
                order.getContrAmount(),
                (int) Double.parseDouble(order.getSalePrice())
        );
    }
}
