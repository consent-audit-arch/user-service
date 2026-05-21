package com.tcc.user_service.infrastructure.persistence.repository;

import com.tcc.user_service.infrastructure.persistence.entity.ContractJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataContractJpaRepository extends JpaRepository<ContractJpaEntity, Long> {
    List<ContractJpaEntity> findByUserId(Long userId);
}
