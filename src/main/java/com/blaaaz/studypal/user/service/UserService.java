package com.blaaaz.studypal.user.service;

import com.blaaaz.studypal.user.model.UserEntity;
import com.blaaaz.studypal.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    // Find user by id or throw 404
    public UserEntity getById(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // Find user by email or throw 404 (case-insensitive)
    public UserEntity getByEmail(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // Return all users
    public List<UserEntity> listAll() {
        return users.findAll();
    }

    // Update profile (firstName, lastName, email). Checks email uniqueness (case-insensitive).
    public UserEntity updateProfile(Long userId, String firstName, String lastName, String email) {
        var u = getById(userId);

        if (email != null && !email.equalsIgnoreCase(u.getEmail())) {
            if (users.existsByEmailIgnoreCase(email)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
            u.setEmail(email.trim());
        }

        if (firstName != null) u.setFirstName(firstName.trim());
        if (lastName  != null) u.setLastName(lastName.trim());

        return users.save(u);
    }

    // Change password (uses PasswordEncoder).
    public void changePassword(Long userId, String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password too short (min 8 chars)");
        }
        var u = getById(userId);
        u.setPasswordhash(passwordEncoder.encode(rawPassword));
        users.save(u);
    }

    // Delete user by id
    public void deleteById(Long userId) {
        var u = getById(userId);
        users.delete(u);
    }
}
