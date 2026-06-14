package hello.orderbridge.pipeline.dto;

import hello.orderbridge.enums.channel.ChannelType;

public record OrderMessage(
        Long orderId, // DB에 저장된 주문 PK
        String channelOrderNo, // 채널 주문 번호
        ChannelType channelType // 어떤 채널에서 온 주문인지
) {
}
