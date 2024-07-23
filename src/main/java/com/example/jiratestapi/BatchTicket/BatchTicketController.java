package com.example.jiratestapi.BatchTicket;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import com.example.jiratestapi.Task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<Batch> getBatchesByProjectId(@PathVariable Long projectId) {
        List<Batch> batchs;
        try {
            Optional<Project> project = projectRepository.findById(projectId);
            batchs=project.get().getBatches();
        } catch (Exception e) {
            throw new RuntimeException("error while getting the batch tickets using the project id");
        }
        return batchs;
    }

    @GetMapping("/batchTicketByProjectName/{projectName}")
    public List<Batch> getBatchesByProjectName(@PathVariable String projectName) {
        try {
            Project project = projectRepository.findProjectByName(projectName);
            List<Batch> batches=project.getBatches();
            return batches;
        } catch (Exception e) {
            throw new RuntimeException("Error while getting the batch tickets using the project name: " + e.getMessage());
        }
    }

    @GetMapping("/TasksByProjectName/{projectName}")
    public List<Task> getTasksByProjectName(@PathVariable String projectName) {
        try {
            Project project = projectRepository.findProjectByName(projectName);
            List<Task> tasks=project.getIssues();
            return tasks;
        } catch (Exception e) {
            throw new RuntimeException("Error while getting the batch tickets using the project name: " + e.getMessage());
        }
    }

}
