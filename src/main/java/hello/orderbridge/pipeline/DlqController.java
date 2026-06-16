package hello.orderbridge.pipeline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dlq")
@RequiredArgsConstructor
@Slf4j
public class DlqController {

    private final DlqService dlqService;

    @GetMapping("/count")
    public Map<String, Integer> getMessageCount() {
        return Map.of("count", dlqService.getMessageCount());
    }

    @PostMapping("/retry")
    public Map<String, Integer> retry() {
        int count = dlqService.retryAll();
        return Map.of("retriedCount", count);
    }
}
