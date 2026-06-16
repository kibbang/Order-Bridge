package hello.orderbridge.collector;

import hello.orderbridge.channel.domain.Channel;
import hello.orderbridge.channel.repository.ChannelRepository;

import hello.orderbridge.collector.dto.RawOrderDto;
import hello.orderbridge.enums.channel.ChannelType;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderCollectScheduler {

    private final List<ChannelCollector> collectors;
    private final ChannelRepository channelRepository;
    private final Map<ChannelType, ChannelCollector> collectorRegistry;
    private final OrderCollectService orderCollectService;
    private final MeterRegistry meterRegistry;

    public OrderCollectScheduler(List<ChannelCollector> collectors, ChannelRepository channelRepository, OrderCollectService orderCollectService, MeterRegistry meterRegistry) {
        this.collectors = collectors;
        this.channelRepository = channelRepository;
        this.collectorRegistry = collectors.stream()
                .collect(Collectors.toMap(ChannelCollector::getChannelType, Function.identity()));
        this.orderCollectService = orderCollectService;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedDelay = 60000)
    public void handleOrderCollect() {
        List<Channel> activeChannels = channelRepository.findByIsActiveTrue();

        activeChannels.forEach(channel -> {
            if (collectorRegistry.containsKey(channel.getType())) {
                ChannelCollector collector = collectorRegistry.get(channel.getType());
                List<RawOrderDto> rawOrders = collector.collect(channel);

                log.info("[{}] {} 건 수집", channel.getType(), rawOrders.size());

                orderCollectService.saveOrders(channel, rawOrders);

                // 수집 성공 이후
                meterRegistry.counter(
                        "order.collected.count",
                        "channel",
                        channel.getType().name()
                )
                        .increment();
            }
        });
    }
}
