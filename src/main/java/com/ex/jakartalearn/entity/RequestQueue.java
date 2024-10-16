package com.ex.jakartalearn.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "request_queue")
public class RequestQueue  extends BaseEntity implements Serializable {

    @OneToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private Request request;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
