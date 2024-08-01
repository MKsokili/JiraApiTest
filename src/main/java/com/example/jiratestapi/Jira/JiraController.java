package com.example.jiratestapi.Jira;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Batch.BatchRepository;
import com.example.jiratestapi.BatchTicket.ActionType;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
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
    private BatchRepository batchRepository;
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
    @PostMapping("/sync")
    public List<BatchTicket> syncTickets() throws Exception {

        List<BatchTicket> batchTickets = jiraService.fetchTickets();





        // Récupération des jiraIds des tickets obtenus depuis Jira
        List<String> jiraIds = batchTickets.stream()
                                      .map(BatchTicket::getJiraId)
                                      .collect(Collectors.toList());

        // Récupération des tickets existants dans la base de données qui ne sont plus présents dans Jira
        List<Task> tasksInDatabase = taskRepository.findAll();
        List<Task> ticketsToDelete = tasksInDatabase.stream()
                                                        .filter(ticket -> !jiraIds.contains(ticket.getJiraId()))
                                                        .collect(Collectors.toList());

        // Suppression des tickets qui ne sont plus présents dans Jira
//        int countDeletedTickets = ticketsToDelete.size();
//        System.out.println("deleted : " +countDeletedTickets);
//        ticketRepository.deleteAll(ticketsToDelete);
        
        // Sauvegarde des tickets dans la base de données

        for (BatchTicket ticket : batchTickets) {
            Optional<Task> existingTicket = taskRepository.findByJiraId(ticket.getJiraId());
            if (existingTicket.isPresent()) {
            ticket.setAction_type(ActionType.UPDATED);
//            batchTicketRepository.save(ticket);


            } else {
                ticket.setAction_type(ActionType.CREATED);
//                batchTicketRepository.save(ticket);
            }


        }


        List<Project> projects = projectRepository.findAll();
        System.out.println("_________________________________________________________________________________________________________________");

        for (Project project : projects) {
            System.out.println("project:" + project.getJiraKey());
            if (project.getJiraKey() == null) {
                continue;
            }

            List<BatchTicket> ticketsList = new ArrayList<>();
            int incr = 0;
            Batch batch = new Batch();
            for (BatchTicket batchTicket : batchTickets) {
                if (project.getJiraKey().equals(batchTicket.getProjectKey())) {
                    System.out.println(incr++ + batchTicket.getProjectKey() + "==" + project.getJiraKey());
                    batchTicket.setBatch(batch);
                    ticketsList.add(batchTicket);
                }
            }

            if (!ticketsList.isEmpty()||ticketsList.get(0).getBatch()!=null) {
                System.out.println("ticketsList:" + ticketsList.get(0).getSummary());
                batch.setBatchTickets(ticketsList);
                batch.setStartedDate(LocalDateTime.now());
                batch.setProject(project); // Assurez-vous d'associer le batch au project
                batchRepository.save(batch);
                project.setName("11111");
                projectRepository.save(project);
            }else{
                System.out.println("else----");
            }
        }
        return batchTickets;

    }


}


//List<BatchTask> tickets = jiraService.fetchTickets();
//
//int countUpdatedTickets = 0;
//int countCreatedTickets = 0 ;
//
//
//
//// Récupération des jiraIds des tickets obtenus depuis Jira
//List<String> jiraIds = tickets.stream()
//        .map(BatchTask::getJiraId)
//        .collect(Collectors.toList());
//
//// Récupération des tickets existants dans la base de données qui ne sont plus présents dans Jira
//List<BatchTask> ticketsInDatabase = ticketRepository.findAll();
//List<BatchTask> ticketsToDelete = ticketsInDatabase.stream()
//        .filter(ticket -> !jiraIds.contains(ticket.getJiraId()))
//        .collect(Collectors.toList());
//
//// Suppression des tickets qui ne sont plus présents dans Jira
//int countDeletedTickets = ticketsToDelete.size();
//        System.out.println("deleted : " +countDeletedTickets);
//        ticketRepository.deleteAll(ticketsToDelete);
//
//// Sauvegarde des tickets dans la base de données
//        for (BatchTask ticket : tickets) {
//Optional<BatchTask> existingTicket = ticketRepository.findByJiraId(ticket.getJiraId());
//            if (existingTicket.isPresent()) {
//// Si le ticket existe déjà, vous pouvez le mettre à jour si nécessaire
//BatchTask ticketToUpdate = existingTicket.get();
//                ticketToUpdate.setProject(ticket.getProject());
//        ticketToUpdate.setTitle(ticket.getTitle());
//        ticketToUpdate.setSummary(ticket.getSummary());
//        ticketToUpdate.setDescription(ticket.getDescription());
//        ticketToUpdate.setStatus(ticket.getStatus());
//        ticketToUpdate.setCreated(ticket.getCreated());
//        ticketToUpdate.setStoryPoints(ticket.getStoryPoints());
//        ticketToUpdate.setAssigneeName(ticket.getAssigneeName());
//        if (!ticketToUpdate.getUpdated().equals(ticket.getUpdated())) {
//        ticketToUpdate.setUpdated(ticket.getUpdated());
//countUpdatedTickets++;
//        }
//        ticketRepository.save(ticketToUpdate);
//            } else {
//                    // Si le ticket n'existe pas, le sauvegarder
//                    ticketRepository.save(ticket);
//countCreatedTickets++ ;
//        }
//        }
//
//        System.out.println("updated : " + countUpdatedTickets) ;
//        System.out.println("created : " + countCreatedTickets) ;
//
//        return tickets;