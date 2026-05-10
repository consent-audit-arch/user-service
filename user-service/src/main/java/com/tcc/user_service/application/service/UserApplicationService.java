package com.tcc.user_service.application.service;

import com.tcc.user_service.application.dto.CreateUserCommand;
import com.tcc.user_service.application.dto.UserResponse;
import com.tcc.user_service.domain.exception.UserNotFoundException;
import com.tcc.user_service.domain.model.User;
import com.tcc.user_service.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserApplicationService {

    private final UserRepository userRepository;

    public UserApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setDocumentNumber(user.getDocumentNumber());
        response.setBirthDate(user.getBirthDate());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
