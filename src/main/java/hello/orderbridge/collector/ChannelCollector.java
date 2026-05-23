package hello.orderbridge.collector;

import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.collector.dto.RawOrderDto;
import hello.orderbridge.enums.channel.ChannelType;

import java.util.List;

public interface ChannelCollector {
    ChannelType getChannelType(); // 이 Collector가 어떤 채널 담당인지 반환
    List<RawOrderDto> collect(Channel channel); // 해당 채널에서 주문 수집 후 List<RawOrderDto> 반환
}
