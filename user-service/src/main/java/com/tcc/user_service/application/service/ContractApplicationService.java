package com.tcc.user_service.application.service;

import com.tcc.user_service.application.dto.UserContractProfileDTO;
import com.tcc.user_service.infrastructure.persistence.entity.ContractJpaEntity;
import com.tcc.user_service.infrastructure.persistence.repository.SpringDataContractJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContractApplicationService {

    private final SpringDataContractJpaRepository repository;

    public ContractApplicationService(SpringDataContractJpaRepository repository) {
        this.repository = repository;
    }

    public List<UserContractProfileDTO> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toDTO)
                .toList();
    }

    private UserContractProfileDTO toDTO(ContractJpaEntity entity) {
        UserContractProfileDTO dto = new UserContractProfileDTO();
        dto.setId(entity.getId());
        dto.setContractType(entity.getContractType());
        dto.setPlanName(entity.getPlanName());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setMonthlyFee(entity.getMonthlyFee());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
