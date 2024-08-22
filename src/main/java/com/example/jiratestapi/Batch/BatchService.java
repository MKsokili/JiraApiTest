package com.example.jiratestapi.Batch;


import com.example.jiratestapi.BatchError.BatchError;
import com.example.jiratestapi.BatchError.BatchErrorRepository;
import com.example.jiratestapi.BatchError.ErrorType;
import com.example.jiratestapi.BatchTicket.ActionType;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import com.example.jiratestapi.Task.Task;
import com.example.jiratestapi.Task.TaskRepository;
import com.example.jiratestapi.users.User;
import com.example.jiratestapi.users.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class BatchService {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    BatchErrorRepository batchErrorRepository;
    public void CheckUpdateTasksFromBatch(BatchTicket ticket  , Task ticketToUpdate){

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
//                        ticket.getBatch().setTicketsUpdated(ticket.getBatch().getTicketsUpdated() + 1);
                ticket.getBatch().incrementTicketsUpdated();
            }
            ticket.setAction_type(ActionType.UPDATED);
            // Update Assignee if the name has changed
            String displayName = ticket.getAssigneeName();
            if (displayName != null) {
                String[] names = displayName.split(" ");
                if (names.length >= 2) {
                    String firstName = names[0];
                    String lastName = names[1];
                    User user = userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
                    if (user != null) {
                        ticketToUpdate.setAssignedTo(user);
                    } else {
                        System.out.println("start in err handling in update");
                        // Log the error if the assignee is not found
                        BatchError error = new BatchError(
                                ticket.getBatch(),
                                ErrorType.ASSIGNEE_NOT_FOUND,
                                "Assignee (" + displayName + ") not found for ticket: " + ticket.getJiraKey(),
                                null
                        );
                        batchErrorRepository.save(error);
                    }
                }
            }
            // Save the updated ticket
            taskRepository.save(ticketToUpdate);

        }else {
            ticket.setAction_type(ActionType.UNCHANGED);
            if (ticket.getBatch() != null) {
//                        ticket.getBatch().setTicketsUnchanged(ticket.getBatch().getTicketsUnchanged() + 1);
                ticket.getBatch().incrementTicketsUnchanged();
            }
        }


    }

    public void CreateTasksFromBatch(BatchTicket ticket){

        Task ticketToCreate = new Task();
        Project prjct = projectRepository.findByJiraKey(ticket.getProjectKey());

        ticketToCreate.setProject(prjct);
        ticketToCreate.setJiraId(ticket.getJiraId());
        ticketToCreate.setJiraKey(ticket.getJiraKey());
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
//                    ticket.getBatch().setTicketsCreated(ticket.getBatch().getTicketsCreated() + 1);
            ticket.getBatch().incrementTicketsCreated();
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
                } else {
                    System.out.println("start in err handling in create");

                    BatchError error = new BatchError(
                            ticket.getBatch(),
                            ErrorType.ASSIGNEE_NOT_FOUND,
                            "Assignee ("+displayName+") not found for ticket: " + ticket.getJiraKey(),
                            null
                    );
                    batchErrorRepository.save(error);
                }
            }
        }

        // Save the ticket
        taskRepository.save(ticketToCreate);
    }
}