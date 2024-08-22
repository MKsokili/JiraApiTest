package com.example.jiratestapi.Task;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.BatchTicket.ActionType;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
@Entity
@Data
@AllArgsConstructor@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jiraId;
    private String jiraKey;
    private String summary;

    @Column(length = 1000)
    private String description;
    private String status;

    private LocalDateTime created;
    private LocalDateTime updated;

    private Double charge;

    private String priority;

    private String comment;

    private Double reevaluatedCharge;

    private Double assignedCharge;


    @ManyToOne
    @JsonBackReference
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
//    @JsonBackReference
    private User assignedTo;


}
