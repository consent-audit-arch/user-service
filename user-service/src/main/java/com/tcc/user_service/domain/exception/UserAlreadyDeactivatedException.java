package com.tcc.user_service.domain.exception;

public class UserAlreadyDeactivatedException extends RuntimeException {
    public UserAlreadyDeactivatedException(Long id) {
        super("User " + id + " is already deactivated.");
    }
}
