package com.tcc.user_service.application.service;

import com.tcc.user_service.domain.exception.UserAlreadyDeactivatedException;
import com.tcc.user_service.domain.exception.UserNotFoundException;
import com.tcc.user_service.domain.model.User;
import com.tcc.user_service.domain.repository.UserRepository;
import com.tcc.user_service.infrastructure.client.ConsentCommandClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConsentCommandClient consentCommandClient;

    private UserApplicationService service;

    @BeforeEach
    void setUp() {
        service = new UserApplicationService(userRepository, consentCommandClient);
    }

    @Test
    void deactivate_shouldSetUserInactiveWhenConsentServiceReturnsSuccess() {
        User user = new User("Test", "test@test.com", "123", null);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(consentCommandClient.deactivate(anyLong(), anyString(), anyLong()))
                .thenReturn(new ConsentCommandClient.DeactivateResult(true, 3));

        service.deactivate(1L);

        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void deactivate_shouldKeepUserActiveWhenConsentServiceThrowsException() {
        User user = new User("Test", "test@test.com", "123", null);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(consentCommandClient.deactivate(anyLong(), anyString(), anyLong()))
                .thenThrow(new RuntimeException("Consent service unavailable"));

        assertThatThrownBy(() -> service.deactivate(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Consent service");

        assertThat(user.isActive()).isTrue();
    }

    @Test
    void deactivate_shouldThrow404WhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deactivate(999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deactivate_shouldThrow409WhenAlreadyDeactivated() {
        User user = new User("Test", "test@test.com", "123", null);
        user.setId(1L);
        user.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.deactivate(1L))
                .isInstanceOf(UserAlreadyDeactivatedException.class);
    }
}
