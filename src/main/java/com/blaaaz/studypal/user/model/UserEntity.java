package com.blaaaz.studypal.user.model;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    private String email;

    @Column(nullable = false, name = "password_hash")
    private String passwordhash;



}
