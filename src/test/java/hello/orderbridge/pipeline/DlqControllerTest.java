package hello.orderbridge.pipeline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DlqController.class)
@WithMockUser
class DlqControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DlqService dlqService;

    @Test
    @DisplayName("DLQ 메시지 수 조회")
    void getMessageCount() throws Exception {
        given(dlqService.getMessageCount()).willReturn(5);

        mockMvc.perform(get("/api/v1/dlq/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    @DisplayName("DLQ 메시지가 없을 때 조회")
    void getMessageCountEmpty() throws Exception {
        given(dlqService.getMessageCount()).willReturn(0);

        mockMvc.perform(get("/api/v1/dlq/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @DisplayName("DLQ 전체 재처리")
    void retryAll() throws Exception {
        given(dlqService.retryAll()).willReturn(3);

        mockMvc.perform(post("/api/v1/dlq/retry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.retriedCount").value(3));
    }
}
