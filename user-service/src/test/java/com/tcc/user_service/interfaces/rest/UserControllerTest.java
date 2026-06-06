package com.tcc.user_service.interfaces.rest;

import com.tcc.user_service.application.dto.batch.BatchUsageResponse;
import com.tcc.user_service.application.dto.batch.BatchUserRequest;
import com.tcc.user_service.application.dto.batch.UserUsageBatchRecord;
import com.tcc.user_service.application.service.ContractApplicationService;
import com.tcc.user_service.application.service.UsageApplicationService;
import com.tcc.user_service.application.service.UserApplicationService;
import com.tcc.user_service.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.tcc.user_service.infrastructure.persistence.repository.SpringDataUserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsageApplicationService usageService;

    @MockitoBean
    private UserApplicationService userService;

    @MockitoBean
    private ContractApplicationService contractService;

    @MockitoBean
    private SpringDataUserJpaRepository userJpaRepository;

    @MockitoBean
    private UserPersistenceMapper userPersistenceMapper;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void findUsageBatch_shouldReturn200WithData() throws Exception {
        BatchUsageResponse response = new BatchUsageResponse(
                List.of(new UserUsageBatchRecord(1L, List.of())),
                List.of()
        );
        when(usageService.findBatch(List.of(1L, 2L))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/batch/usage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\": [1, 2]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value(1))
                .andExpect(jsonPath("$.denied").isEmpty())
                .andExpect(jsonPath("$.partial").value(false));
    }

    @Test
    void findUsageBatch_withEmptyIds_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/users/batch/usage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\": []}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findUsageBatch_withoutIds_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/users/batch/usage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
