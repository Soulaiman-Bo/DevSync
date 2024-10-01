package com.ex.entity;

import com.ex.Enums.Role;
import jakarta.persistence.*;

@Entity
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    /*
 TODO [Reverse Engineering] create field to map the 'manager' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "manager", columnDefinition = "role not null")
    private Object manager;
*/
}