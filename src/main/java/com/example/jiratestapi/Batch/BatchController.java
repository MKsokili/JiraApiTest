package com.example.jiratestapi.Batch;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/batch")
public class BatchController {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/{projectKey}/{date}")
    public Batch getProjectBatch(@PathVariable String projectKey , @PathVariable String date) {

        Project project = projectRepository.findByJiraKey(projectKey);


        // Convert the date string to LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate batchDate = LocalDate.parse(date, formatter);

        // Find the batch by project and date (assuming you use LocalDate for the date part)
        Optional<Batch> batchProject = batchRepository.findByProjectAndStartedDate(project, batchDate);

        if (batchProject.isPresent()) {
            // Gérer le cas où aucun résultat n'est trouvé
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No batch found for the given project and date");
            return batchProject.get();
        }else{
            return null;
        }


    }

    @GetMapping("/all")
    public List<Batch> getProjectBatches() {
        return batchRepository.findAll();
    }
    
    
}
