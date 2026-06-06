package com.tcc.user_service.application.service;

import com.tcc.security.aspect.ConsentAuthorizationAspect;
import com.tcc.security.pip.PipTitularResult;
import com.tcc.user_service.application.dto.UserUsageProfileDTO;
import com.tcc.user_service.application.dto.batch.BatchUsageResponse;
import com.tcc.user_service.infrastructure.persistence.entity.UsageRecordJpaEntity;
import com.tcc.user_service.infrastructure.persistence.repository.SpringDataUsageRecordJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsageApplicationServiceTest {

    @Mock
    private SpringDataUsageRecordJpaRepository repository;

    @InjectMocks
    private UsageApplicationService service;

    @Test
    void findBatch_withDecisions_shouldFilterAuthorizedAndDenied() {
        try (MockedStatic<ConsentAuthorizationAspect> aspectMock = Mockito.mockStatic(ConsentAuthorizationAspect.class)) {
            List<PipTitularResult> decisions = List.of(
                    new PipTitularResult(1L, true, null),
                    new PipTitularResult(2L, true, null),
                    new PipTitularResult(3L, false, "No consent")
            );
            aspectMock.when(ConsentAuthorizationAspect::getDecisionsFromRequest).thenReturn(decisions);

            UsageRecordJpaEntity record1 = createRecord(100L, 1L, "bandwidth", new BigDecimal("100.00"), "GB");
            UsageRecordJpaEntity record2 = createRecord(101L, 2L, "storage", new BigDecimal("50.00"), "GB");
            when(repository.findByUserIdIn(List.of(1L, 2L))).thenReturn(List.of(record1, record2));

            BatchUsageResponse result = service.findBatch(List.of(1L, 2L, 3L));

            assertThat(result.getData()).hasSize(2);
            assertThat(result.getData().get(0).getUserId()).isEqualTo(1L);
            assertThat(result.getData().get(0).getUsageRecords()).hasSize(1);
            assertThat(result.getData().get(0).getUsageRecords().get(0).getServiceType()).isEqualTo("bandwidth");
            assertThat(result.getData().get(1).getUserId()).isEqualTo(2L);
            assertThat(result.getData().get(1).getUsageRecords()).hasSize(1);
            assertThat(result.getData().get(1).getUsageRecords().get(0).getServiceType()).isEqualTo("storage");
            assertThat(result.getDenied()).hasSize(1);
            assertThat(result.getDenied().get(0).getId()).isEqualTo(3L);
            assertThat(result.getDenied().get(0).getReason()).isEqualTo("No consent");
            assertThat(result.isPartial()).isTrue();
        }
    }

    @Test
    void findBatch_withDecisionsAllAuthorized_shouldHaveNoDenied() {
        try (MockedStatic<ConsentAuthorizationAspect> aspectMock = Mockito.mockStatic(ConsentAuthorizationAspect.class)) {
            List<PipTitularResult> decisions = List.of(
                    new PipTitularResult(1L, true, null),
                    new PipTitularResult(2L, true, null)
            );
            aspectMock.when(ConsentAuthorizationAspect::getDecisionsFromRequest).thenReturn(decisions);

            UsageRecordJpaEntity record = createRecord(100L, 1L, "bandwidth", new BigDecimal("100.00"), "GB");
            when(repository.findByUserIdIn(List.of(1L, 2L))).thenReturn(List.of(record));

            BatchUsageResponse result = service.findBatch(List.of(1L, 2L));

            assertThat(result.getData()).hasSize(2);
            assertThat(result.getDenied()).isEmpty();
            assertThat(result.isPartial()).isFalse();
        }
    }

    @Test
    void findBatch_withDecisionsAllDenied_shouldReturnNoData() {
        try (MockedStatic<ConsentAuthorizationAspect> aspectMock = Mockito.mockStatic(ConsentAuthorizationAspect.class)) {
            List<PipTitularResult> decisions = List.of(
                    new PipTitularResult(1L, false, "No consent"),
                    new PipTitularResult(2L, false, "Expired")
            );
            aspectMock.when(ConsentAuthorizationAspect::getDecisionsFromRequest).thenReturn(decisions);

            BatchUsageResponse result = service.findBatch(List.of(1L, 2L));

            assertThat(result.getData()).hasSize(0);
            assertThat(result.getDenied()).hasSize(2);
            assertThat(result.isPartial()).isTrue();
        }
    }

    @Test
    void findBatch_withoutDecisions_shouldReturnAllAsAuthorized() {
        List<Long> requestedIds = List.of(1L, 2L);

        UsageRecordJpaEntity record1 = createRecord(100L, 1L, "bandwidth", new BigDecimal("100.00"), "GB");
        UsageRecordJpaEntity record2 = createRecord(101L, 2L, "storage", new BigDecimal("50.00"), "GB");
        when(repository.findByUserIdIn(requestedIds)).thenReturn(List.of(record1, record2));

        BatchUsageResponse result = service.findBatch(requestedIds);

        assertThat(result.getData()).hasSize(2);
        assertThat(result.getDenied()).isEmpty();
        assertThat(result.isPartial()).isFalse();
    }

    private static UsageRecordJpaEntity createRecord(Long id, Long userId, String serviceType,
                                                      BigDecimal amount, String unit) {
        UsageRecordJpaEntity entity = new UsageRecordJpaEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setServiceType(serviceType);
        entity.setUsageAmount(amount);
        entity.setUsageUnit(unit);
        entity.setRecordedAt(Instant.now());
        entity.setCreatedAt(Instant.now());
        return entity;
    }
}
