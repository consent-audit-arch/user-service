package com.tcc.user_service.infrastructure.persistence.mapper;

import com.tcc.user_service.domain.model.User;
import com.tcc.user_service.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setFullName(user.getFullName());
        entity.setEmail(user.getEmail());
        entity.setDocumentNumber(user.getDocumentNumber());
        entity.setBirthDate(user.getBirthDate());
        entity.setCreatedAt(user.getCreatedAt());
        return entity;
    }

    public User toDomain(UserJpaEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setFullName(entity.getFullName());
        user.setEmail(entity.getEmail());
        user.setDocumentNumber(entity.getDocumentNumber());
        user.setBirthDate(entity.getBirthDate());
        user.setCreatedAt(entity.getCreatedAt());
        return user;
    }
}
