package com.example.jiratestapi.Batch;

import com.example.jiratestapi.BatchError.BatchError;
import com.example.jiratestapi.Projects.Project;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startedDate;
    private Boolean  isCompleted;
    private Integer ticketsDeleted;
    private Integer ticketsUpdated;
    private Integer ticketsCreated;
    private Integer ticketsUnchanged;
    private Integer totalTicketsSync;

    //add errors fields and statistiques and so
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BatchTicket> batchTickets ;
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BatchError> batchErrors ;
    @ManyToOne
//    @JsonBackReference
    @JsonIgnoreProperties(value={"issues" , "batches"})
    private Project project;

    public Batch(Long id, LocalDate startedDate, Boolean isCompleted, Integer ticketsDeleted,
                 Integer ticketsUpdated, Integer ticketsCreated, Integer ticketsUnchanged,
                 Integer totalTicketsSync, List<BatchTicket> batchTickets, List<BatchError> batchErrors,
                 Project project) {
        this.id = id;
        this.startedDate = startedDate;
        this.isCompleted = isCompleted;
        this.ticketsDeleted = ticketsDeleted;
        this.ticketsUpdated = ticketsUpdated;
        this.ticketsCreated = ticketsCreated;
        this.ticketsUnchanged = ticketsUnchanged;
        this.totalTicketsSync = totalTicketsSync;
        this.batchTickets = batchTickets;
        this.batchErrors = batchErrors;
        this.project = project;
    }

    public void incrementTicketsUpdated() {
        this.ticketsUpdated += 1;
    }
    public void incrementTicketsCreated() {
        this.ticketsCreated += 1;
    }
    public void incrementTicketsUnchanged() {
        this.ticketsUnchanged += 1;
    }

}
