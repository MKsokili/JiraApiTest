package com.example.jiratestapi.BatchError;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Batch.BatchRepository;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/batch-error")
public class BatchErrorController {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private BatchErrorRepository batchErrorRepository;

    @PostMapping("/{error_id}/check")
    public ResponseEntity<String> checkError(@PathVariable Long error_id) {
        try {
            Optional<BatchError> optionalError = batchErrorRepository.findById(error_id);

            if (optionalError.isPresent()) {
                BatchError error = optionalError.get();
                error.setChecked(true); // Assuming `setChecked` instead of `setIsChecked`
                batchErrorRepository.save(error);
                return ResponseEntity.ok("The error with ID: " + error_id + " is checked");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error with ID: " + error_id + " not found");
            }

        } catch (Exception e) {
            // Replace System.out with proper logging
            // log.error("Exception occurred while checking error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }

    @PostMapping("/check-all")
    @Transactional
    public ResponseEntity<String> checkAll() {
        try {
            List<BatchError> errorsList = batchErrorRepository.findAll();

            if (!errorsList.isEmpty()) {
                errorsList.stream().forEach(e->{
                    e.setChecked(true);
                });

                return ResponseEntity.ok("Done");

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(" no item found");
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }





}
