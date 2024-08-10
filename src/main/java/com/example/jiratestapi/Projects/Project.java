package com.example.jiratestapi.Projects;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.example.jiratestapi.Task.Task;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jiraKey;
    private String name;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private  List<Batch> batches;
    @OneToMany(mappedBy = "project")
    @JsonManagedReference
    List<Task> issues;
    public void addBatch(Batch batch) {
        batches.add(batch);
        batch.setProject(this);  // Correction ici
    }
}
