package com.example.jiratestapi.Batch;

import com.example.jiratestapi.BatchTicket.BatchTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/")
public class BatchController {
    @Autowired
    private BatchRepository batchRepository;
    @GetMapping("/batchTicketsOfLastBatch")
    public List<BatchTicket> getBatchTicketsOfLastBatch() {
        List<BatchTicket> batchTickets = new ArrayList<>();
        try {
            List<Batch> batches = batchRepository.findAllBatchesOrderedByStartedDateDesc();

            if (!batches.isEmpty()) {
                //first batch
                Batch lastBatch = batches.get(0);
                batchTickets = lastBatch.getBatchTickets();
            }
        } catch (Exception exception) {
            throw new RuntimeException("Error while getting tasks of the last batch", exception);
        }
        return batchTickets;
    }


}
