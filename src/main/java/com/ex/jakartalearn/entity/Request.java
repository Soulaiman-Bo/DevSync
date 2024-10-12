package com.ex.jakartalearn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "requests")
public class Request extends BaseEntity implements Serializable{

    @ManyToOne
    private User user;

    @OneToOne
    private Task task;

    LocalDateTime createdAt;

    Boolean isAccepted;

    public Request() {

    }

}
