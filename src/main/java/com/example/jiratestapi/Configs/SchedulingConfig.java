package com.example.jiratestapi.Configs;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Batch.BatchRepository;
import com.example.jiratestapi.Batch.BatchService;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import com.example.jiratestapi.Task.Task;
import com.example.jiratestapi.BatchTicket.ActionType;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.example.jiratestapi.BatchTicket.BatchTicketRepository;
import com.example.jiratestapi.Task.TaskRepository;
import com.example.jiratestapi.users.User;
import com.example.jiratestapi.users.UserRepository;

import jakarta.transaction.Transactional;
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
    private BatchService batchService;

    @Autowired
    private JiraController ticketController;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired

    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private BatchTicketRepository batchTicketRepository;
    // @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
//    @Scheduled(cron = "0 */1 * * * ?")
    public void syncTicketsNightly() throws Exception {
        System.out.println("start in batch");
        List<BatchTicket> tasks= ticketController.syncTickets();


        if (tasks.isEmpty() || tasks.get(0).getBatch() == null) {
            System.out.println("No tasks or batch is null");
            return;
        }


        System.out.println("start in saving tasks to db after batch");

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

        // Récupération des jiraIds des tickets à supprimer
        List<String> jiraIdsToDelete = ticketsToDelete.stream()
                .map(Task::getJiraId)
                .collect(Collectors.toList());



        // Suppression des tickets qui ne sont plus présents dans Jira
        int countDeletedTickets = ticketsToDelete.size();
        System.out.println("deleted : " + countDeletedTickets);
        ticketsToDelete.forEach(task -> task.setStatus("Archived"));
//        taskRepository.deleteAll(ticketsToDelete);


        // Sauvegarde des tickets dans la base de données
        for (BatchTicket ticket : tasks) {
            Optional<Task> existingTicket = taskRepository.findByJiraId(ticket.getJiraId());

            if (existingTicket.isPresent()) {
                // Si le ticket existe déjà, vous pouvez le mettre à jour si nécessaire
                Task ticketToUpdate = existingTicket.get();

                batchService.CheckUpdateTasksFromBatch(ticket, ticketToUpdate);

            } else {

                batchService.CreateTasksFromBatch(ticket);
            }
        }

    }
}

