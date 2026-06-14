package hello.orderbridge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@Slf4j
/**
 * Spring AMQP의 기본 철학인 "게으른 생성(Lazy Declaration)"
 * 방식이기 때문에 Admin UI에서 아무 작업이 없으면 커넥션 조차도 생성해두지 않음
 *
 * 실제 메시지 발행(Publisher), 메시지 수신 대기(Consumer)일때 생긴다.
 *
 * Why?
 * > 서버 자원의 절약과 유연한 연결을 하기 위함
 */
public class RabbitMqConfig {

    // Exchange 이름 상수
    public static final String ORDERS_EXCHANGE = "orders.exchange";

    // Queue 이름 상수
    public static final String ORDERS_QUEUE = "orders.inbound";

    // Routing Key 상수 (Exchange → Queue 연결 키)
    public static final String ORDERS_ROUTING_KEY = "orders.inbound";

    public RabbitMqConfig() {
        // 이건 그냥 찍어두자.. 불안하니까
        log.info("RabbitMqConfig 클래스가 스프링에 로드");
    }

    /**
     * RabbitAdmin Bean 등록 — Exchange/Queue를 RabbitMQ에 실제로 선언하는 담당자
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * ① Exchange Bean 등록
     * DirectExchange = 라우팅 키가 정확히 일치하는 큐로만 전달 (큐 이름을 라우팅 키로 쓰는게 가장 단순)
     */
    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(ORDERS_EXCHANGE);
    }

    /**
     * ② Queue Bean 등록
     */
    @Bean
    public Queue ordersQueue() {
        return new Queue(ORDERS_QUEUE);
    }

    /**
     * ③ Exchange와 Queue를 Routing Key로 연결
     */
    @Bean
    public Binding ordersBinding(DirectExchange ordersExchange, Queue ordersQueue) {
        return BindingBuilder.bind(ordersQueue) // 이 큐(우체통)를 정해두고 나서 (연결작업 시작)
                .to(ordersExchange) // 이 Exchange (우편물 취급소)에 부칠건데
                .with(ORDERS_ROUTING_KEY); // ORDERS_ROUTING_KEY 라는 주소가 적힌 것만 분류 해라
    }

    @Bean
    public MessageConverter jacksonJsonMessageConverter (ObjectMapper objectMapper) {
        // Jackson2JsonMessageConverter를 objectMapper로 생성해서 return
        return new JacksonJsonMessageConverter((JsonMapper) objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        // 1. RabbitTemplate을 connectionFactory로 생성
        // 2. setMessageConverter()로 JSON 컨버터 세팅
        // 3. return

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}