package com.example.jiratestapi.BatchError;

import java.time.LocalDateTime;
import java.util.Date;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.BatchTicket.ActionType;
import com.example.jiratestapi.Projects.Project;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "batch_id")
    private Batch batch  ;
    private ErrorType errorType;
    private String message;
    private LocalDateTime timestamp;
    private Long taskId;

    public BatchError(Batch batch, ErrorType errorType, String message,  Long taskId) {
        this.batch = batch;
        this.errorType = errorType;
        this.message = message;
        this.taskId = taskId;
    }
    // Getters and setters

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

}