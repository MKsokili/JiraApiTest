package com.example.jiratestapi.BatchTicket;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import com.example.jiratestapi.Task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/batchTickets")
public class BatchTicketController {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private BatchTicketRepository batchTicketRepository;


    @GetMapping("/{id}")
    public ResponseEntity<BatchTicket> getBatchTicketById(@PathVariable Long id) {
        BatchTicket batchTicket = batchTicketRepository.findById(id).get();
        if (batchTicket != null) {
            return ResponseEntity.ok(batchTicket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

