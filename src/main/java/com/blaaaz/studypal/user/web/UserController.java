package com.blaaaz.studypal.user.web;


import com.blaaaz.studypal.user.model.UserEntity;
import com.blaaaz.studypal.user.service.UserService;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService user;

    public UserController(UserService user) {
        this.user = user;
    }

    // get list of all users
    public List<UserEntity> getAllUsers() {
        return user.listAll();
    }

    // get current user
    @GetMapping("/me")
    public UserEntity getCurrentUser(@RequestParam long id) {
        return user.getById(id);
    }

    // get one user by id
    @GetMapping("/{id}")
    public UserEntity getUser(@RequestParam long id) {
        return user.getById(id);
    }

    // update user profil
    @GetMapping("/{id}")
    public UserEntity uptadeUser(@RequestParam long id,
                                 @RequestParam(required = false) String firstName,
                                 @RequestParam(required = false) String lastName,
                                 @RequestParam(required = false) String email) {
        return user.updateProfile(id, firstName, lastName, email);
    }

    // change password
    @GetMapping("/{id}/password")
    public ResponseEntity<String> changePassword(@RequestParam long id,
                                                 @RequestParam String rawPassword) {
        user.changePassword(id, rawPassword);
        return ResponseEntity.ok("Password changed");
    }

    //Delete user by id
    @GetMapping("/{id}/delete")
    public ResponseEntity<String> deleteUser(@RequestParam long id) {
        user.deleteById(id);
        return ResponseEntity.ok("User deleted");
    }
}
