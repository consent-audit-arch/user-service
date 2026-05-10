package com.tcc.user_service.domain.model;

import java.time.Instant;
import java.time.LocalDate;

public class User {
    private Long id;
    private String fullName;
    private String email;
    private String documentNumber;
    private LocalDate birthDate;
    private Instant createdAt;

    public User() {
    }

    public User(String fullName, String email, String documentNumber, LocalDate birthDate) {
        this.fullName = fullName;
        this.email = email;
        this.documentNumber = documentNumber;
        this.birthDate = birthDate;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
