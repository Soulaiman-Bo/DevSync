package com.ex.jakartalearn.entity;

import com.ex.jakartalearn.enumeration.RequestType;
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
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "request_type")
    private RequestType requestType;

    @Column(nullable = false, name = "is_accepted")
    private Boolean isAccepted;

    public Request() {

    }

}
