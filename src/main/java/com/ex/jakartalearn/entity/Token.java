package com.ex.jakartalearn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "tokens")
public class Token extends BaseEntity implements Serializable {

    @Column(nullable = false)
    private Integer refuse_token;

    @Column(nullable = false)
    private Integer delete_token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    public Token() {
        this.delete_token = 1;
        this.refuse_token = 2;
    }

    public Token(User user) {
        this();
        this.user = user;
    }
}
