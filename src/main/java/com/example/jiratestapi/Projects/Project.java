package com.example.jiratestapi.Projects;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.example.jiratestapi.Task.Task;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Project {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jiraKey;
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private  List<Batch> batches;

    @OneToMany(mappedBy = "project")
    @JsonManagedReference
    List<Task> issues;
    public void addBatch(Batch batch) {
        batches.add(batch);
        batch.setProject(this);  // Correction ici
    }
}
