package com.ex.entity;

import com.ex.Enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users", schema = "jakarta")
public class User {
    @Column(name = "username", nullable = false, length = 225)
    private String username;

    @Lob
    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Lob
    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Lob
    @Column(name = "email", nullable = false)
    private String email;

    @Lob
    @Column(name = "password", nullable = false)
    private String password;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public User(String username, String firstname, String lastname, String email, String password) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;

    }

    public User() {

    }


}