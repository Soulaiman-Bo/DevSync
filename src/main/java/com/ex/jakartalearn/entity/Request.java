package com.ex.jakartalearn.entity;

import com.ex.jakartalearn.enumeration.RequestType;
import com.ex.jakartalearn.enumeration.TaskStatus;
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "request_type")
    private RequestType requestType;

    @Column(nullable = false, name = "is_accepted")
    private Boolean isAccepted;

    @Column(nullable = false, name = "is_fulfilled")
    private Boolean isFulfilled;

    @Column(nullable = false, name = "is_queued")
    private Boolean isQueued;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL)
    private RequestQueue request;

    public Request() {

    }

}
