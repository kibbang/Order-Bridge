package hello.orderbridge.order.dto;

import hello.orderbridge.enums.channel.ChannelType;
import hello.orderbridge.enums.order.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class OrderSearchCondition {
    private ChannelType channelType;
    private OrderStatus status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
