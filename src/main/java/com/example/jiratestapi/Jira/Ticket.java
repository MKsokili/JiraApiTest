package com.example.jiratestapi.Jira;

import java.time.LocalDateTime;
import java.util.Date;

import io.micrometer.common.lang.Nullable;
import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
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

    private Integer storyPoints;

    private String assigneeName; // Changement de champ pour inclure le nom de l'assignee


}