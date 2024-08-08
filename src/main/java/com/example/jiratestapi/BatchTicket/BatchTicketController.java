package com.example.jiratestapi.BatchTicket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/batchTickets")
public class BatchTicketController {


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



