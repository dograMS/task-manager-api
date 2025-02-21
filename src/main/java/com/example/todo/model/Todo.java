package com.example.todo.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Todo implements Comparable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne
    @JoinColumn
    @JsonBackReference
    private UserLogin user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createDateTime;

    @Column(nullable = false)
    private LocalDateTime dueDateTime;

    @Override
    public int compareTo(Object o) {
        UUID obj = (UUID)o;
        return uuid.compareTo(obj);
    }
}


// @Column(nullable = false, columnDefinition = "VARCHAR(20) CONSTRAINT status_check CHECK (status IN('Done','InProgress','Pending')) DEFAULT 'Pending'")

