package com.blaaaz.studypal.user.web;

import com.blaaaz.studypal.user.model.UserEntity;
import com.blaaaz.studypal.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService user;

    public UserController(UserService user) {
        this.user = user;
    }

    // all users (keep only if you really want it exposed)
    @GetMapping
    public List<UserEntity> getAllUsers() {
        return user.listAll();
    }

    // current user from JWT
    @GetMapping("/me")
    public ResponseEntity<UserEntity> getCurrentUser(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(user.getById(userId));
    }

    // one user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUser(@PathVariable long id) {
        return ResponseEntity.ok(user.getById(id));
    }

    // update profile of current user
    @PutMapping("/me")
    public ResponseEntity<UserEntity> updateMe(
            Authentication auth,
            @RequestBody Map<String, String> body
    ) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(
                user.updateProfile(userId, body.get("firstName"), body.get("lastName"), body.get("email"))
        );
    }

    // change password of current user
    @PatchMapping("/me/password")
    public ResponseEntity<String> changeMyPassword(Authentication auth, @RequestBody Map<String, String> body) {
        Long userId = (Long) auth.getPrincipal();
        user.changePassword(userId, body.get("password"));
        return ResponseEntity.ok("Password changed");
    }

    // delete current user
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMe(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        user.deleteById(userId);
        return ResponseEntity.ok("User deleted");
    }
}
