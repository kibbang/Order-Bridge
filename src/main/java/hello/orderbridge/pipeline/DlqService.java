package hello.orderbridge.pipeline;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import hello.orderbridge.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static hello.orderbridge.config.RabbitMqConfig.*;

@Service
@RequiredArgsConstructor
public class DlqService {

    private final RabbitTemplate rabbitTemplate;

    // DLQ 메시지 카운트 조회
    public int getMessageCount() {
        Integer count = rabbitTemplate.execute(channel -> {
            DeclareOk result = channel.queueDeclarePassive(ORDERS_DLQ);
            return result.getMessageCount();
        });

        return count != null ? count : 0;
    }

    // DLQ 메시지 1건을 꺼내서 원래 큐로 재발행
    public boolean retry() {
        Message receivedMessage = rabbitTemplate.receive(ORDERS_DLQ);

        if (receivedMessage == null) {
            return false;
        }

        rabbitTemplate.send(ORDERS_EXCHANGE, ORDERS_ROUTING_KEY, receivedMessage);
        return true;
    }

    // DLQ 전체 메시지를 원래 큐로 발행
    public int retryAll() {
        int count = 0;

        while (retry()) {
            count++;
        }

        return count;
    }
}
