package com.example.jiratestapi.Projects;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Tasks.Issue;
import com.example.jiratestapi.Tasks.Task;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Project {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jira_key;
    private String name;

    @OneToMany(mappedBy = "project")
    private  List<Batch> Batches;
    @OneToMany(mappedBy = "project")
    List<Issue> issues;



}
