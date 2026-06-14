package hello.orderbridge.wms;

import hello.orderbridge.wms.dto.WmsRequest;
import hello.orderbridge.wms.dto.WmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
@Slf4j
public class WmsClient {

    private final Random random = new Random();

    public WmsResponse send(WmsRequest request) {
        log.info("[WMS] 전송시도: {}", request.channelOrderNo());

        if (random.nextInt(100) < 80) {
            String wmsOrderNo = "WMS-" + UUID.randomUUID();
            log.info("[WMS] 전송 성공: {} -> {}", request.channelOrderNo(), wmsOrderNo);
            return new WmsResponse(true, wmsOrderNo, null);
        }

        log.warn("[WMS] 전송 실패: {}", request.channelOrderNo());
        return new WmsResponse(false, null, "WMS 서버 응답 없음");
    }
}
