package com.tcc.user_service.infrastructure.persistence.repository;

import com.tcc.user_service.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataUserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
}
