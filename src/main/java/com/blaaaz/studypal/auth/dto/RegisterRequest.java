package com.blaaaz.studypal.auth.dto;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getEmail() { return this.email; }
    public String getPassword() { return this.password; }
}
