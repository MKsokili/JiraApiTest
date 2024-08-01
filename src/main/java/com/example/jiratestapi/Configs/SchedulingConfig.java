package com.example.jiratestapi.Configs;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import com.example.jiratestapi.Task.Task;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.example.jiratestapi.Task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.jiratestapi.Jira.JiraController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Autowired
    private JiraController ticketController;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;

    // @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "0 */1 * * * ?")
    public void syncTicketsNightly() throws Exception {
       List<BatchTicket> tasks= ticketController.syncTickets();

        System.out.println("start in saving tasks to db after batch");
        System.out.println("task 1:" +tasks.get(0).getSummary());
int countUpdatedTickets = 0;
int countCreatedTickets = 0 ;
// Récupération des jiraIds des tickets obtenus depuis Jira
List<String> jiraIds = tasks.stream()
        .map(BatchTicket::getJiraId)
        .collect(Collectors.toList());

// Récupération des tickets existants dans la base de données qui ne sont plus présents dans Jira
        List<Task> ticketsInDatabase = taskRepository.findAll();
        List<Task> ticketsToDelete = new ArrayList<>();

        if (ticketsInDatabase != null && !ticketsInDatabase.isEmpty()) {
            ticketsToDelete = ticketsInDatabase.stream()
                    .filter(ticket -> !jiraIds.contains(ticket.getJiraId()))
                    .collect(Collectors.toList());
        } else {
            // Handle the case where the task repository returns null or an empty list
            System.out.println("No tasks found in the database.");
            // Additional handling logic can be added here if needed
        }
// Suppression des tickets qui ne sont plus présents dans Jira
int countDeletedTickets = ticketsToDelete.size();
        System.out.println("deleted : " +countDeletedTickets);
        taskRepository.deleteAll(ticketsToDelete);

// Sauvegarde des tickets dans la base de données
        for (BatchTicket ticket : tasks) {
Optional<Task> existingTicket = taskRepository.findByJiraId(ticket.getJiraId());
            if (existingTicket.isPresent()) {
// Si le ticket existe déjà, vous pouvez le mettre à jour si nécessaire
Task ticketToUpdate = existingTicket.get();

        ticketToUpdate.setTitle(ticket.getTitle());
        ticketToUpdate.setSummary(ticket.getSummary());
        ticketToUpdate.setDescription(ticket.getDescription());
        ticketToUpdate.setStatus(ticket.getStatus());
        ticketToUpdate.setCreated(ticket.getCreated());
        ticketToUpdate.setStoryPoints(ticket.getStoryPoints());
        ticketToUpdate.setAssigneeName(ticket.getAssigneeName());
//        if (!ticketToUpdate.getUpdated().equals(ticket.getUpdated())) {
        ticketToUpdate.setUpdated(ticket.getUpdated());
//countUpdatedTickets++;
//        }
        taskRepository.save(ticketToUpdate);
            } else {
                Task ticketToCreate = new Task();
                Project prjct=projectRepository.findByJiraKey(ticket.getProjectKey());

                ticketToCreate.setProject(prjct);
                ticketToCreate.setTitle(ticket.getTitle());
                ticketToCreate.setJiraId(ticket.getJiraId());
                ticketToCreate.setSummary(ticket.getSummary());
                ticketToCreate.setDescription(ticket.getDescription());
                ticketToCreate.setStatus(ticket.getStatus());
                ticketToCreate.setCreated(ticket.getCreated());
                ticketToCreate.setUpdated(ticket.getUpdated());
                ticketToCreate.setStoryPoints(ticket.getStoryPoints());
                ticketToCreate.setAssigneeName(ticket.getAssigneeName());
                    // Si le ticket n'existe pas, le sauvegarder
                taskRepository.save(ticketToCreate);
countCreatedTickets++ ;
        }
        }

        System.out.println("updated : " + countUpdatedTickets) ;
        System.out.println("created : " + countCreatedTickets) ;

    }
}

