package com.example.jiratestapi.BatchTicket;

import java.time.LocalDateTime;
import java.util.Date;

import com.example.jiratestapi.Batch.Batch;
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
public class BatchTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jiraId;
    private String projectKey;
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
    @JoinColumn(name = "batch_id")
    @JsonBackReference
    private Batch batch  ;

}