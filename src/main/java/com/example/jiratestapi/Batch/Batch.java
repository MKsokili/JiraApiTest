package com.example.jiratestapi.Batch;

import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table
@NoArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startedDate;
    private Boolean  isCompleted;
    //add errors fields and statistiques and so
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BatchTicket> batchTickets ;
    @ManyToOne
    @JsonBackReference
    private Project project;

}
