package hello.orderbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication

@EnableScheduling
public class OrderBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderBridgeApplication.class, args);
    }

}
