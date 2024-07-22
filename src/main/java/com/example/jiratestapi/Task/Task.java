package com.example.jiratestapi.Task;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.BatchTicket.ActionType;
import com.example.jiratestapi.Projects.Project;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jiraId;
    private String title;
    private String summary;
    private String description;
    private String status;

    private LocalDateTime created;
    private LocalDateTime updated;
    private String assigneeName; //Changement de champ pour inclure le nom de l'assignee

    private Integer storyPoints;


    private ActionType action_type;
    private Date action_date;

    @ManyToOne
    private Project project;


}
