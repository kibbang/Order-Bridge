package hello.orderbridge.collector.impl;

import hello.orderbridge.collector.dto.RawOrderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuctionCollectorTest {
    @Autowired
    AuctionCollector auctionCollector;

    @Test
    void 옥션_목업_데이터_읽기() {
        // Given

        // When
        List<RawOrderDto> result = auctionCollector.collect(null);

        // Then
        // 힌트1. result가 비어있지 않은지 확인
        // 힌트2. 첫 번째 주문의 channelOrderNo가 null이 아닌지 확인
        // 힌트3. 첫 번째 주문의 items가 비어있지 않은지 확인

        assertNotNull(result);
        assertNotNull(result.get(0).channelOrderNo());
        assertNotNull(result.get(0).items());
    }
}