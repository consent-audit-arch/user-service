package com.tcc.user_service.application.service;

import com.tcc.user_service.application.dto.UserUsageProfileDTO;
import com.tcc.user_service.infrastructure.persistence.entity.UsageRecordJpaEntity;
import com.tcc.user_service.infrastructure.persistence.repository.SpringDataUsageRecordJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
