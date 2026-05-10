package com.tcc.user_service.infrastructure.persistence.repository;

import com.tcc.user_service.domain.model.User;
import com.tcc.user_service.domain.repository.UserRepository;
import com.tcc.user_service.infrastructure.persistence.entity.UserJpaEntity;
import com.tcc.user_service.infrastructure.persistence.mapper.UserPersistenceMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserJpaRepository springDataRepository;
    private final UserPersistenceMapper mapper;

    public JpaUserRepositoryAdapter(SpringDataUserJpaRepository springDataRepository, UserPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(java.time.Instant.now());
        }
        UserJpaEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return springDataRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
