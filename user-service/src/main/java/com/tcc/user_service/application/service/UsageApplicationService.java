package com.tcc.user_service.application.service;

import com.tcc.user_service.application.dto.UserUsageProfileDTO;
import com.tcc.user_service.application.dto.batch.BatchUsageResponse;
import com.tcc.user_service.application.dto.batch.DeniedUser;
import com.tcc.user_service.application.dto.batch.UserUsageBatchRecord;
import com.tcc.user_service.infrastructure.persistence.entity.UsageRecordJpaEntity;
import com.tcc.user_service.infrastructure.persistence.repository.SpringDataUsageRecordJpaRepository;
import com.tcc.security.aspect.ConsentAuthorizationAspect;
import com.tcc.security.pip.PipTitularResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UsageApplicationService {

    private final SpringDataUsageRecordJpaRepository repository;

    public UsageApplicationService(SpringDataUsageRecordJpaRepository repository) {
        this.repository = repository;
    }

    public List<UserUsageProfileDTO> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toDTO)
                .toList();
    }

    public BatchUsageResponse findBatch(List<Long> requestedIds) {
        List<PipTitularResult> decisions = ConsentAuthorizationAspect.getDecisionsFromRequest();

        List<Long> authorizedIds;
        List<DeniedUser> denied;

        if (!decisions.isEmpty()) {
            authorizedIds = decisions.stream()
                    .filter(PipTitularResult::isAuthorized)
                    .map(PipTitularResult::getTitularId)
                    .toList();
            denied = decisions.stream()
                    .filter(d -> !d.isAuthorized())
                    .map(d -> new DeniedUser(d.getTitularId(), d.getReason()))
                    .toList();
        } else {
            authorizedIds = requestedIds;
            denied = List.of();
        }

        List<UsageRecordJpaEntity> allRecords = repository.findByUserIdIn(authorizedIds);
        Map<Long, List<UserUsageProfileDTO>> grouped = allRecords.stream()
                .collect(Collectors.groupingBy(
                        UsageRecordJpaEntity::getUserId,
                        Collectors.mapping(this::toDTO, Collectors.toList())
                ));

        List<UserUsageBatchRecord> data = new ArrayList<>();
        for (Long userId : authorizedIds) {
            List<UserUsageProfileDTO> records = grouped.getOrDefault(userId, List.of());
            data.add(new UserUsageBatchRecord(userId, records));
        }

        return new BatchUsageResponse(data, denied);
    }

    private UserUsageProfileDTO toDTO(UsageRecordJpaEntity entity) {
        UserUsageProfileDTO dto = new UserUsageProfileDTO();
        dto.setId(entity.getId());
        dto.setServiceType(entity.getServiceType());
        dto.setUsageAmount(entity.getUsageAmount());
        dto.setUsageUnit(entity.getUsageUnit());
        dto.setRecordedAt(entity.getRecordedAt());
        return dto;
    }
}
