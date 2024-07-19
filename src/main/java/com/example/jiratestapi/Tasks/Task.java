package com.example.jiratestapi.Tasks;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Projects.Project;
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
    @JoinColumn(name = "batch_id")
    private Batch batch  ;

    @ManyToOne
    private Project project;
}