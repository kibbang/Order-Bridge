package hello.orderbridge.collector;

import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.channel.repository.ChannelRepository;
import hello.orderbridge.collector.dto.RawOrderDto;
import hello.orderbridge.collector.dto.RawOrderItemDto;
import hello.orderbridge.enums.channel.ChannelType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCollectSchedulerTest {

    @Test
    void 활성_채널의_주문을_수집하고_저장한다() {
        // Given
        ChannelRepository channelRepository = mock(ChannelRepository.class);
        OrderCollectService orderCollectService = mock(OrderCollectService.class);

        Channel coupang = Channel.of("쿠팡", ChannelType.COUPANG, "key-1");

        ChannelCollector coupangCollector = mock(ChannelCollector.class);
        given(coupangCollector.getChannelType()).willReturn(ChannelType.COUPANG);

        List<RawOrderDto> mockOrders = List.of(
                new RawOrderDto("ORD-001", "홍길동", "010-1234-5678",
                        "김수령", "010-9876-5432", "서울시 강남구", "문 앞에",
                        30000, LocalDateTime.now(),
                        List.of(new RawOrderItemDto("P001", "상품A", "S001", 1, 30000)))
        );
        given(coupangCollector.collect(coupang)).willReturn(mockOrders);
        given(channelRepository.findByIsActiveTrue()).willReturn(List.of(coupang));

        OrderCollectScheduler scheduler = new OrderCollectScheduler(
                List.of(coupangCollector), channelRepository, orderCollectService
        );

        // When
        scheduler.handleOrderCollect();

        // Then
        verify(coupangCollector).collect(coupang);
        verify(orderCollectService).saveOrders(coupang, mockOrders);
    }

    @Test
    void 등록되지_않은_채널타입은_수집하지_않는다() {
        // Given
        ChannelRepository channelRepository = mock(ChannelRepository.class);
        OrderCollectService orderCollectService = mock(OrderCollectService.class);

        Channel smartStore = Channel.of("스마트스토어", ChannelType.SMART_STORE, "key-2");

        ChannelCollector coupangCollector = mock(ChannelCollector.class);
        given(coupangCollector.getChannelType()).willReturn(ChannelType.COUPANG);

        given(channelRepository.findByIsActiveTrue()).willReturn(List.of(smartStore));

        OrderCollectScheduler scheduler = new OrderCollectScheduler(
                List.of(coupangCollector), channelRepository, orderCollectService
        );

        // When
        scheduler.handleOrderCollect();

        // Then
        verify(coupangCollector, never()).collect(any());
        verify(orderCollectService, never()).saveOrders(any(), any());
    }

    @Test
    void 활성_채널이_없으면_아무것도_수집하지_않는다() {
        // Given
        ChannelRepository channelRepository = mock(ChannelRepository.class);
        OrderCollectService orderCollectService = mock(OrderCollectService.class);

        ChannelCollector coupangCollector = mock(ChannelCollector.class);
        given(coupangCollector.getChannelType()).willReturn(ChannelType.COUPANG);

        given(channelRepository.findByIsActiveTrue()).willReturn(List.of());

        OrderCollectScheduler scheduler = new OrderCollectScheduler(
                List.of(coupangCollector), channelRepository, orderCollectService
        );

        // When
        scheduler.handleOrderCollect();

        // Then
        verify(coupangCollector, never()).collect(any());
        verify(orderCollectService, never()).saveOrders(any(), any());
    }
}
