package com.example.jiratestapi.Jira;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Batch.BatchRepository;
import com.example.jiratestapi.BatchError.BatchError;
import com.example.jiratestapi.BatchError.BatchErrorRepository;
import com.example.jiratestapi.BatchError.ErrorType;
import com.example.jiratestapi.BatchTicket.ActionType;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import com.example.jiratestapi.Projects.ProjectService;
import com.example.jiratestapi.Task.Task;
import com.example.jiratestapi.BatchTicket.BatchTicketRepository;
import com.example.jiratestapi.Task.TaskRepository;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping ("/")
@AllArgsConstructor
public class JiraController {
    JiraService jiraService;

    private BatchTicketRepository batchTicketRepository;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private ProjectService projectService;
    private BatchRepository batchRepository;
    private BatchErrorRepository batchErrorRepository;
    @GetMapping("/create")
    public ResponseEntity<String> createIssue(){
        try{
            jiraService.createIssue("SCRUM", "created from backend","a demo description", "Task");
            return new ResponseEntity<>("created successfully", HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("err in Try", HttpStatus.BAD_REQUEST);

        }

    }
    @GetMapping("/get")
    public ResponseEntity<Object> getLastIssues(){
        try{
           Object jira= jiraService.getIssuesCreatedInLastFiveMinutes();
            return new ResponseEntity<>(jira, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("err in Try", HttpStatus.BAD_REQUEST);

        }

    }

    @GetMapping("/tickets")
    public List<BatchTicket> getTickets() throws Exception {
        return jiraService.fetchTickets();
    }

    @GetMapping("/mine")
    public List<BatchTicket> getMyTickets() throws Exception {
        return jiraService.fetchTicketsAssignedToMyEmail();
    }

    @GetMapping("/fetchByProject/{projectKey}")
    public List<BatchTicket> getMyProjectTickets(@PathVariable String projectKey) throws Exception {
        return jiraService.fetchTicketsByProject(projectKey);
    }

//    @Transactional
    // @PostMapping("/sync")
    public List<BatchTicket> syncTickets() throws Exception {
        List<BatchTicket> batchTickets = new ArrayList<>();
        List<Project> projects = projectRepository.findAll();
        List<String> keys =new ArrayList<>();
        try {
            batchTickets = jiraService.fetchTickets();

            for (Project project : projects) {
                System.out.println("project:" + project.getJiraKey());
                if (project.getJiraKey() == null||!projectService.doesProjectKeyExist(project.getJiraKey())) {
                    System.out.println("the project is not valid jira key"+project.getJiraKey());
                    continue;
                }
                keys.add(project.getJiraKey());
                Batch batch = new Batch();
                System.out.println("the project is valid =>"+project.getJiraKey());

                List<Task> tasksByProjectId = taskRepository.findAllByProjectIdAndStatusNot(project.getId(), "Archived");
                List<BatchTicket> ticketsList = new ArrayList<>();
                List<Task> ticketsToDelete = new ArrayList<>();

                int countSyncTickets = 0;

                for (BatchTicket batchTicket : batchTickets) {
                    if (project.getJiraKey().equals(batchTicket.getProjectKey())) {
                        countSyncTickets++;
                        batchTicket.setBatch(batch);
                        ticketsList.add(batchTicket);
                    }
                }

                List<String> jiraIds = ticketsList.stream()
                        .map(BatchTicket::getJiraId)
                        .collect(Collectors.toList());

                ticketsToDelete = tasksByProjectId.stream()
                        .filter(task -> !jiraIds.contains(task.getJiraId()))
                        .collect(Collectors.toList());

                if (!ticketsList.isEmpty()) {
                    System.out.println("ticketsList:" + ticketsList.get(0).getSummary());
                    batch.setBatchTickets(ticketsList);
                    batch.setStartedDate(LocalDate.now());
                    batch.setTicketsCreated(0);
                    batch.setTicketsUpdated(0);
                    batch.setTotalTicketsSync(countSyncTickets);
                    batch.setTicketsDeleted(ticketsToDelete.size());
                    batch.setTicketsUnchanged(0);
                    batch.setProject(project);
                    batch.setIsCompleted(true);

                    batchRepository.save(batch);
                    projectRepository.save(project);
                } else {
                    System.out.println("else----");
                    batch.setIsCompleted(false);
                }
            }
            batchTickets.removeIf(e -> !keys.contains(e.getProjectKey()));

        } catch (Exception e) {
            // Handle the exception and log it to BatchError

            for (Project project : projects) {
                if (project.getJiraKey() == null||!project.getIsValid()) {
                    continue;
                }
                Batch batch = new Batch();
                BatchError batchError = new BatchError();
                batchError.setBatch(batch);
                batchError.setErrorType(ErrorType.BATCH_SYNC_ERROR);
                batchError.setMessage(e.getMessage());
                batchError.setTimestamp(LocalDateTime.now());

                batch.setIsCompleted(false);
                batch.setStartedDate(LocalDate.now());
                batch.setProject(project);

                batchErrorRepository.save(batchError);
                batchRepository.save(batch);
                projectRepository.save(project);

            }
        }

        return batchTickets;
    }



}


