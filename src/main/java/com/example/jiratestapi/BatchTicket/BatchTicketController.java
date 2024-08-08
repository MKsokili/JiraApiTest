package com.example.jiratestapi.BatchTicket;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class BatchTicketController {
    @Autowired
    private ProjectRepository projectRepository;
    @GetMapping("/batchTicketByProjectId/{projectId}")
    public List<Batch> getBatchTicketsByProjectId(@PathVariable Long projectId) {
        List<Batch> batchs;
        try {
            Optional<Project> project = projectRepository.findById(projectId);
            batchs=project.get().getBatches();
        } catch (Exception e) {
            throw new RuntimeException("error while getting the batch tickets using the project id");
        }
        return batchs;
    }

}
