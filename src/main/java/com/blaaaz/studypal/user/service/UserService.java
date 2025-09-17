package com.blaaaz.studypal.user.service;

import com.blaaaz.studypal.user.model.UserEntity;
import com.blaaaz.studypal.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository users;

    // Currently no password encoder (plain text storage).
    // Change later to use PasswordEncoder (e.g., BCryptPasswordEncoder) for security.
    public UserService(UserRepository users) {
        this.users = users;
    }

    // Find user by id or throw 404
    public UserEntity getById(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // Find user by email or throw 404
    public UserEntity getByEmail(String email) {
        return users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // Return all users
    public List<UserEntity> listAll() {
        return users.findAll();
    }

    // Update profile (firstName, lastName, email). Checks email uniqueness.
    public UserEntity updateProfile(Long userId, String firstName, String lastName, String email) {
        var u = getById(userId);

        if (email != null && !email.equalsIgnoreCase(u.getEmail())) {
            if (users.existsByEmail(email)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
            u.setEmail(email);
        }

        if (firstName != null) u.setFirstName(firstName);
        if (lastName  != null) u.setLastName(lastName);

        return users.save(u);
    }

    // Change password (currently stored as plain text).
    // Replace with encoder.encode(rawPassword) after adding PasswordEncoder.
    public void changePassword(Long userId, String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password too short (min 8 chars)");
        }
        var u = getById(userId);
        u.setPasswordhash(rawPassword);
        users.save(u);
    }

    // Delete user by id
    public void deleteById(Long userId) {
        var u = getById(userId);
        users.delete(u);
    }
}
