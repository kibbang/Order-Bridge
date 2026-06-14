package hello.orderbridge.order.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@Slf4j
@RequestMapping("/api/sse")
public class OrderSseController {

    private static final Long TIMEOUT_LIMIT = 60000L;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter orders() {

        log.info("SSE 연결 요청");

        SseEmitter emitter = new SseEmitter(TIMEOUT_LIMIT);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    public void sendOrderUpdate(Long orderId, String status) {
        // 뭘 보낼지, 실패한 emitter 처리는 어떻게 할지 생각해보고 구현
        // sendOrderUpdate에서 emitters를 순회하면서 emitter.send()를 호출하면 되는데, send 실패한 emitter는 목록에서 제거
        emitters.forEach(emitter ->  {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("order-update").data(orderId + ": " + status)
                );
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        });
    }
}
