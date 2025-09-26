package com.blaaaz.studypal.auth.service;

import com.blaaaz.studypal.auth.dto.AuthResponse;
import com.blaaaz.studypal.auth.dto.LoginRequest;
import com.blaaaz.studypal.auth.dto.RegisterRequest;
import com.blaaaz.studypal.auth.util.JwtUtil;
import com.blaaaz.studypal.user.model.UserEntity;
import com.blaaaz.studypal.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // register new user
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (req == null || isBlank(req.getEmail()) || isBlank(req.getPassword())
                || isBlank(req.getFirstName()) || isBlank(req.getLastName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }

        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        UserEntity user = new UserEntity();
        user.setFirstName(req.getFirstName().trim());
        user.setLastName(req.getLastName().trim());
        user.setEmail(req.getEmail().trim());
        user.setPasswordhash(passwordEncoder.encode(req.getPassword()));

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(String.valueOf(user.getId()), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    // login existing user
    public AuthResponse login(LoginRequest req) {
        if (req == null || isBlank(req.getEmail()) || isBlank(req.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing email or password");
        }

        UserEntity user = userRepository.findByEmailIgnoreCase(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordhash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtUtil.generateToken(String.valueOf(user.getId()), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
