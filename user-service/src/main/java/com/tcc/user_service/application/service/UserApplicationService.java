package com.tcc.user_service.application.service;

import com.tcc.user_service.application.dto.CreateUserCommand;
import com.tcc.user_service.application.dto.UserResponse;
import com.tcc.user_service.application.dto.batch.BatchUserResponse;
import com.tcc.user_service.application.dto.batch.DeniedUser;
import com.tcc.user_service.domain.exception.UserAlreadyDeactivatedException;
import com.tcc.user_service.domain.exception.UserNotFoundException;
import com.tcc.user_service.domain.model.User;
import com.tcc.user_service.domain.repository.UserRepository;
import com.tcc.user_service.infrastructure.client.ConsentCommandClient;
import com.tcc.security.aspect.ConsentAuthorizationAspect;
import com.tcc.security.pip.PipTitularResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserApplicationService {

    private final UserRepository userRepository;
    private final ConsentCommandClient consentCommandClient;

    public UserApplicationService(UserRepository userRepository,
                                   ConsentCommandClient consentCommandClient) {
        this.userRepository = userRepository;
        this.consentCommandClient = consentCommandClient;
    }

    public UserResponse create(CreateUserCommand command) {
        LocalDate birthDate = command.getBirthDate() != null && !command.getBirthDate().isEmpty()
                ? LocalDate.parse(command.getBirthDate())
                : null;

        User user = new User(
                command.getFullName(),
                command.getEmail(),
                command.getDocumentNumber(),
                birthDate
        );

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toResponse(user);
    }

    @Transactional
    public DeactivateUserResponse deactivate(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.isActive()) {
            throw new UserAlreadyDeactivatedException(userId);
        }

        ConsentCommandClient.DeactivateResult result = consentCommandClient.deactivate(
                userId, "SYSTEM", 0L);

        user.setActive(false);
        userRepository.save(user);

        return new DeactivateUserResponse(userId, "DEACTIVATED", result.consentsRevoked());
    }

    @Transactional(readOnly = true)
    public BatchUserResponse findBatch(List<Long> requestedIds) {
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

        List<User> users = userRepository.findAllById(authorizedIds);
        List<UserResponse> data = users.stream().map(this::toResponse).toList();

        return new BatchUserResponse(data, denied);
    }

    public record DeactivateUserResponse(Long userId, String status, int consentsRevoked) {}

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setDocumentNumber(user.getDocumentNumber());
        response.setBirthDate(user.getBirthDate());
        response.setCreatedAt(user.getCreatedAt());
        response.setActive(user.isActive());
        return response;
    }
}
