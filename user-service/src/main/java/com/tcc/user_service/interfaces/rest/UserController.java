package com.tcc.user_service.interfaces.rest;

import com.tcc.security.annotation.RequiresConsent;
import com.tcc.user_service.application.dto.CreateUserCommand;
import com.tcc.user_service.application.dto.UserResponse;
import com.tcc.user_service.application.dto.batch.BatchUserRequest;
import com.tcc.user_service.application.dto.batch.BatchUserResponse;
import com.tcc.user_service.application.service.UserApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserApplicationService userService;

    public UserController(UserApplicationService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserCommand command) {
        UserResponse response = userService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @RequiresConsent(resource = "USER_PROFILE", action = "READ")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Void> exists(@PathVariable Long id) {
        return userService.findById(id) != null
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/batch")
    @RequiresConsent(resource = "USER_PROFILE", action = "READ",
            dataCategories = {"PERSONAL_DATA"})
    public ResponseEntity<BatchUserResponse> findBatch(@Valid @RequestBody BatchUserRequest request) {
        BatchUserResponse response = userService.findBatch(request.getIds());
        return ResponseEntity.ok(response);
    }
}
