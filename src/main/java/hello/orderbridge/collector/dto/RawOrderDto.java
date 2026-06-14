package hello.orderbridge.collector.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public record RawOrderDto(
        String channelOrderNo,      // 채널에서 부여한 주문번호 (중복 체크 키로 사용)
        String ordererName,         // 주문자 이름
        String ordererPhone,        // 주문자 연락처
        String receiverName,        // 수령인 이름
        String receiverPhone,       // 수령인 연락처
        String deliveryAddress,     // 배송지 주소
        String deliveryMemo,        // 배송 메모
        Integer totalAmount,        // 주문 총액
        LocalDateTime orderedAt,    // 주문 일시
        List<RawOrderItemDto>items  // 주문 상품 목록 List<RawOrderItemDto>
) {
}
