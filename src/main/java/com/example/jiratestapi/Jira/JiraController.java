package com.example.jiratestapi.Jira;

import com.example.jiratestapi.Tasks.Task;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping ("/")
@AllArgsConstructor
public class JiraController {
    JiraService jiraService;

    private TicketRepository ticketRepository;
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
    public List<Task> getTickets() throws Exception {
        return jiraService.fetchTickets();
    }

    @GetMapping("/mine")
    public List<Task> getMyTickets() throws Exception {
        return jiraService.fetchTicketsAssignedToMyEmail();
    }

    @GetMapping("/fetchByProject/{projectKey}")
    public List<Task> getMyProjectTickets(@PathVariable String projectKey) throws Exception {
        return jiraService.fetchTicketsByProject(projectKey);
    }

    @Transactional
    @PostMapping("/sync")
    public ResponseEntity<String> syncTickets() throws Exception {

        List<Task> tickets = jiraService.fetchTickets();

        int countUpdatedTickets = 0;
        int countCreatedTickets = 0 ;



        // Récupération des jiraIds des tickets obtenus depuis Jira
        List<String> jiraIds = tickets.stream()
                                      .map(Task::getJiraId)
                                      .collect(Collectors.toList());

        // Récupération des tickets existants dans la base de données qui ne sont plus présents dans Jira
        List<Task> ticketsInDatabase = ticketRepository.findAll();
        List<Task> ticketsToDelete = ticketsInDatabase.stream()
                                                        .filter(ticket -> !jiraIds.contains(ticket.getJiraId()))
                                                        .collect(Collectors.toList());

        // Suppression des tickets qui ne sont plus présents dans Jira
        int countDeletedTickets = ticketsToDelete.size();
        System.out.println("deleted : " +countDeletedTickets);
        ticketRepository.deleteAll(ticketsToDelete);                                               
        
        // Sauvegarde des tickets dans la base de données
        for (Task ticket : tickets) {
            Optional<Task> existingTicket = ticketRepository.findByJiraId(ticket.getJiraId());
            if (existingTicket.isPresent()) {
                // Si le ticket existe déjà, vous pouvez le mettre à jour si nécessaire
                Task ticketToUpdate = existingTicket.get();
                ticketToUpdate.setProject(ticket.getProject());
                ticketToUpdate.setTitle(ticket.getTitle());
                ticketToUpdate.setSummary(ticket.getSummary());
                ticketToUpdate.setDescription(ticket.getDescription());
                ticketToUpdate.setStatus(ticket.getStatus());
                ticketToUpdate.setCreated(ticket.getCreated());
                ticketToUpdate.setStoryPoints(ticket.getStoryPoints());
                ticketToUpdate.setAssigneeName(ticket.getAssigneeName());
                if (!ticketToUpdate.getUpdated().equals(ticket.getUpdated())) {
                    ticketToUpdate.setUpdated(ticket.getUpdated());
                    countUpdatedTickets++;
                }
                ticketRepository.save(ticketToUpdate);
            } else {
                // Si le ticket n'existe pas, le sauvegarder
                ticketRepository.save(ticket);
                countCreatedTickets++ ;
            }
        }

        System.out.println("updated : " + countUpdatedTickets) ;
        System.out.println("created : " + countCreatedTickets) ;

        return ResponseEntity.ok("Tickets synchronized successfully");

    }


}

