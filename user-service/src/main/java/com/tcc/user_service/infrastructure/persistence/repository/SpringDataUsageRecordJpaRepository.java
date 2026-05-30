package com.tcc.user_service.infrastructure.persistence.repository;

import com.tcc.user_service.infrastructure.persistence.entity.UsageRecordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataUsageRecordJpaRepository extends JpaRepository<UsageRecordJpaEntity, Long> {
    List<UsageRecordJpaEntity> findByUserId(Long userId);
    List<UsageRecordJpaEntity> findByUserIdIn(List<Long> userIds);
}
