package com.example.jiratestapi.Configs;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Batch.BatchRepository;
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
    @Scheduled(cron = "0 */1 * * * ?")
    public void syncTicketsNightly() throws Exception {
        List<BatchTicket> tasks= ticketController.syncTickets();

//       Optional<Batch> optionalBatch = batchRepository.findById(tasks.get(0).getBatch().getId());
//
//
//        System.out.println("start in saving tasks to db after batch");
//        System.out.println("task 1:" +tasks.get(0).getSummary());
//int countUpdatedTickets = 0;
//int countCreatedTickets = 0 ;
//// Récupération des jiraIds des tickets obtenus depuis Jira
//List<String> jiraIds = tasks.stream()
//        .map(BatchTicket::getJiraId)
//        .collect(Collectors.toList());
//
//// Récupération des tickets existants dans la base de données qui ne sont plus présents dans Jira
//        List<Task> ticketsInDatabase = taskRepository.findAll();
//        List<Task> ticketsToDelete = new ArrayList<>();
//
//        if (ticketsInDatabase != null && !ticketsInDatabase.isEmpty()) {
//            ticketsToDelete = ticketsInDatabase.stream()
//                    .filter(ticket -> !jiraIds.contains(ticket.getJiraId()))
//                    .collect(Collectors.toList());
//        } else {
//            // Handle the case where the task repository returns null or an empty list
//            System.out.println("No tasks found in the database.");
//            // Additional handling logic can be added here if needed
//        }
//
//// Récupération des jiraIds des tickets à supprimer
//List<String> jiraIdsToDelete = ticketsToDelete.stream()
//        .map(Task::getJiraId)
//        .collect(Collectors.toList());
//
//// Debug statement to ensure we have the correct jiraIds to delete
//System.out.println("Jira IDs to delete: " + jiraIdsToDelete);
//
//// Mise à jour de actionType des BatchTicket correspondants
//List<BatchTicket> batchTicketsToDelete = tasks.stream()
//        .filter(task -> jiraIdsToDelete.contains(task.getJiraId()))
//        .collect(Collectors.toList());
//
//// Debug statement to ensure we have the correct BatchTickets to update
//tasks.forEach(task -> {
//        System.out.println("Task Jira ID: " + task.getJiraId());
//    });
//System.out.println("BatchTickets to update: " + batchTicketsToDelete);
//
//batchTicketsToDelete.forEach(batchTicket -> {
//    batchTicket.setAction_type(ActionType.DELETED);
//    System.out.println("Setting actionType to DELETED for BatchTicket with JiraId: " + batchTicket.getJiraId());
//});
//
//// Persist the updated BatchTicket objects if needed
//batchTicketRepository.saveAll(batchTicketsToDelete);
//
//// Suppression des tickets qui ne sont plus présents dans Jira
//int countDeletedTickets = ticketsToDelete.size();
//        System.out.println("deleted : " +countDeletedTickets);
//        taskRepository.deleteAll(ticketsToDelete);
//
//// Sauvegarde des tickets dans la base de données
//        for (BatchTicket ticket : tasks) {
//           Optional<Task> existingTicket = taskRepository.findByJiraId(ticket.getJiraId());
//
//             if (existingTicket.isPresent()) {
//                // Si le ticket existe déjà, vous pouvez le mettre à jour si nécessaire
//                Task ticketToUpdate = existingTicket.get();
//
//                boolean dateChanged = ticket.getUpdated().isAfter(ticketToUpdate.getUpdated());
//
//                if (dateChanged) {
//
//                        ticketToUpdate.setTitle(ticket.getTitle());
//                        ticketToUpdate.setSummary(ticket.getSummary());
//                        ticketToUpdate.setDescription(ticket.getDescription());
//                        ticketToUpdate.setStatus(ticket.getStatus());
//                        ticketToUpdate.setCreated(ticket.getCreated());
//                        ticketToUpdate.setStoryPoints(ticket.getStoryPoints());
//                        ticketToUpdate.setAssigneeName(ticket.getAssigneeName());
//                        ticketToUpdate.setComment(ticket.getComment());
//                        ticketToUpdate.setPriority(ticket.getPriority());
//                        //if (!ticketToUpdate.getUpdated().equals(ticket.getUpdated())) {
//                        ticketToUpdate.setUpdated(ticket.getUpdated());
//                        ticket.getBatch().setTicketsUpdated(ticket.getBatch().getTicketsUpdated() + 1);
//                        ticket.setAction_type(ActionType.UPDATED);
//                        countUpdatedTickets++;
//
//                        // Update Assign ticket to user
//
//                        String displayName = ticket.getAssigneeName();
//                        if (displayName != null) {
//                                String[] names = displayName.split(" ");
//                                if (names.length >= 2) {
//                                        String firstName = names[0];
//                                        String lastName = names[1];
//                                        User user = userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
//                                        if (user != null) {
//                                                ticketToUpdate.setAssignedTo(user);
//                                        }
//                                }
//                        }
//                        taskRepository.save(ticketToUpdate);
//                }
//
////        }
//
//            } else {
//                Task ticketToCreate = new Task();
//                Project prjct=projectRepository.findByJiraKey(ticket.getProjectKey());
//
//                ticketToCreate.setProject(prjct);
//                ticketToCreate.setTitle(ticket.getTitle());
//                ticketToCreate.setJiraId(ticket.getJiraId());
//                ticketToCreate.setSummary(ticket.getSummary());
//                ticketToCreate.setDescription(ticket.getDescription());
//                ticketToCreate.setStatus(ticket.getStatus());
//                ticketToCreate.setCreated(ticket.getCreated());
//                ticketToCreate.setUpdated(ticket.getUpdated());
//                ticketToCreate.setStoryPoints(ticket.getStoryPoints());
//                ticketToCreate.setAssigneeName(ticket.getAssigneeName());
//                ticketToCreate.setComment(ticket.getComment());
//                ticketToCreate.setPriority(ticket.getPriority());
//                 ticket.getBatch().setTicketsCreated(ticket.getBatch().getTicketsCreated() + 1);
//                 ticket.setAction_type(ActionType.CREATED);
//
//                // Assign ticket to user
//
//                String displayName = ticket.getAssigneeName();
//                if (displayName != null) {
//                    String[] names = displayName.split(" ");
//                    if (names.length >= 2) {
//                        String firstName = names[0];
//                        String lastName = names[1];
//                        User user = userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
//                        if (user != null) {
//                            ticketToCreate.setAssignedTo(user);
//                        }
//                    }
//                }
//
//                    // Si le ticket n'existe pas, le sauvegarder
//                taskRepository.save(ticketToCreate);
//countCreatedTickets++ ;
//        }
//        }
//
//
////        if (optionalBatch.isPresent()) {
////                Batch batch =  optionalBatch.get();
////                batch.setTicketsCreated(countCreatedTickets);
////                batch.setTicketsUpdated(countUpdatedTickets);
////                batch.setTicketsDeleted(countDeletedTickets);
////
////                batchRepository.save(batch);
////        }
//
//
//        System.out.println("updated : " + countUpdatedTickets) ;
//        System.out.println("created : " + countCreatedTickets) ;
//
//    }
        if (tasks.isEmpty() || tasks.get(0).getBatch() == null) {
            System.out.println("No tasks or batch is null");
            return;
        }

        Optional<Batch> optionalBatch = batchRepository.findById(tasks.get(0).getBatch().getId());

        System.out.println("start in saving tasks to db after batch");
        System.out.println("task 1:" +tasks.get(0).getSummary());
        int countUpdatedTickets = 0;
        int countCreatedTickets = 0;

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

        // Debug statement to ensure we have the correct jiraIds to delete
        System.out.println("Jira IDs to delete: " + jiraIdsToDelete);

        // Mise à jour de actionType des BatchTicket correspondants
        List<BatchTicket> batchTicketsToDelete = tasks.stream()
                .filter(task -> jiraIdsToDelete.contains(task.getJiraId()))
                .collect(Collectors.toList());

//        System.out.println("BatchTickets to update: " + batchTicketsToDelete);

//        batchTicketsToDelete.forEach(batchTicket -> {
//            batchTicket.setAction_type(ActionType.DELETED);
//            System.out.println("Setting actionType to DELETED for BatchTicket with JiraId: " + batchTicket.getJiraId());
//        });

        // Persist the updated BatchTicket objects if needed
//        batchTicketRepository.saveAll(batchTicketsToDelete);

        // Suppression des tickets qui ne sont plus présents dans Jira
        int countDeletedTickets = ticketsToDelete.size();
        System.out.println("deleted : " + countDeletedTickets);
        taskRepository.deleteAll(ticketsToDelete);

        // Sauvegarde des tickets dans la base de données
        for (BatchTicket ticket : tasks) {
            Optional<Task> existingTicket = taskRepository.findByJiraId(ticket.getJiraId());

            if (existingTicket.isPresent()) {
                // Si le ticket existe déjà, vous pouvez le mettre à jour si nécessaire
                Task ticketToUpdate = existingTicket.get();

                boolean dateChanged = ticket.getUpdated().isAfter(ticketToUpdate.getUpdated());

                if (dateChanged) {

                    ticketToUpdate.setSummary(ticket.getSummary());
                    ticketToUpdate.setDescription(ticket.getDescription());
                    ticketToUpdate.setStatus(ticket.getStatus());
                    ticketToUpdate.setCreated(ticket.getCreated());
                    ticketToUpdate.setCharge(ticket.getCharge());
                    ticketToUpdate.setComment(ticket.getComment());
                    ticketToUpdate.setPriority(ticket.getPriority());
                    //if (!ticketToUpdate.getUpdated().equals(ticket.getUpdated())) {
                    ticketToUpdate.setUpdated(ticket.getUpdated());
                    if (ticket.getBatch() != null) {
                        ticket.getBatch().setTicketsUpdated(ticket.getBatch().getTicketsUpdated() + 1);
                    }
                    ticket.setAction_type(ActionType.UPDATED);
                    countUpdatedTickets++;

                    // Update Assign ticket to user

                    String displayName = ticket.getAssigneeName();
                    if (displayName != null) {
                        String[] names = displayName.split(" ");
                        if (names.length >= 2) {
                            String firstName = names[0];
                            String lastName = names[1];
                            User user = userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
                            if (user != null) {
                                ticketToUpdate.setAssignedTo(user);
                            }
                        }
                    }
                    taskRepository.save(ticketToUpdate);
                }else {
                    ticket.setAction_type(ActionType.UNCHANGED);
                    if (ticket.getBatch() != null) {
                        ticket.getBatch().setTicketsUnchanged(ticket.getBatch().getTicketsUnchanged() + 1);
                    }
                }

            } else {
                Task ticketToCreate = new Task();
                Project prjct = projectRepository.findByJiraKey(ticket.getProjectKey());

                ticketToCreate.setProject(prjct);
                ticketToCreate.setJiraId(ticket.getJiraId());
                ticketToCreate.setSummary(ticket.getSummary());
                ticketToCreate.setDescription(ticket.getDescription());
                ticketToCreate.setStatus(ticket.getStatus());
                ticketToCreate.setCreated(ticket.getCreated());
                ticketToCreate.setUpdated(ticket.getUpdated());
                ticketToCreate.setCharge(ticket.getCharge());
                ticketToCreate.setAssignedCharge(ticket.getAssignedCharge());
                ticketToCreate.setReevaluatedCharge(ticket.getReevaluatedCharge());
                ticketToCreate.setComment(ticket.getComment());
                ticketToCreate.setPriority(ticket.getPriority());
                if (ticket.getBatch() != null) {
                    ticket.getBatch().setTicketsCreated(ticket.getBatch().getTicketsCreated() + 1);
                }
                ticket.setAction_type(ActionType.CREATED);

                // Assign ticket to user

                String displayName = ticket.getAssigneeName();
                if (displayName != null) {
                    String[] names = displayName.split(" ");
                    if (names.length >= 2) {
                        String firstName = names[0];
                        String lastName = names[1];
                        User user = userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
                        if (user != null) {
                            ticketToCreate.setAssignedTo(user);
                        }
                    }
                }

                // Si le ticket n'existe pas, le sauvegarder
                taskRepository.save(ticketToCreate);
                countCreatedTickets++;
            }
        }

                System.out.println("updated : " + countUpdatedTickets) ;
        System.out.println("created : " + countCreatedTickets) ;
    }
}

