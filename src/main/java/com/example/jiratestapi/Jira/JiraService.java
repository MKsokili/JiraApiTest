package com.example.jiratestapi.Jira;

import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import com.example.jiratestapi.SyncAuth.SyncAuth;
import com.example.jiratestapi.SyncAuth.SyncAuthService;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class JiraService {

     private RestTemplate restTemplate;
     private ProjectRepository projectRepository;
     private SyncAuthService syncAuthService;




    private HttpHeaders createHeaders() {
         SyncAuth syncAuth=syncAuthService.getSyncAuth();
        System.out.println("the Sync is---------------------------------------------------------------------------------------------------------------------------------------------------------------------- :"+syncAuth);
        String auth = syncAuth.getEmail() + ":" + syncAuth.getToken();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    public String getIssuesCreatedInLastWeek() {
        SyncAuth syncAuth=syncAuthService.getSyncAuth();

        String url = syncAuth.getApi_url() + "/search?jql=created >= -7d";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    public String createIssue(String projectKey, String summary, String description, String issueType) {
        SyncAuth syncAuth=syncAuthService.getSyncAuth();

        String url = syncAuth.getApi_url() + "/issue";
        HttpHeaders headers = createHeaders();
        String requestBody = String.format(
                "{ \"fields\": { \"project\": { \"key\": \"%s\" }, \"summary\": \"%s\", \"description\": \"%s\", \"issuetype\": { \"name\": \"%s\" } } }",
                projectKey, summary, description, issueType);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getStatusCode() == HttpStatus.CREATED ? response.getBody() : "Error creating issue: " + response.getStatusCode();
    }
    public Object getIssuesCreatedInLastFiveMinutes() {
        // JQL query to get issues created in the last 5 minutes
        String jql = "created >= -5m";
        SyncAuth syncAuth=syncAuthService.getSyncAuth();
        String url = syncAuth.getApi_url()+ "/search?jql=";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
                // JQL query to get issues created in the last 5 minutes
            // SyncAuth syncAuth=syncAuthService.getSyncAuth();

            //     String jql = "created >= -5m";
            //     String fields = "comment , assignee"; // Specify the fields you need, e.g., "comment"
            //     String url = syncAuth.getApi_url() + "/search?jql=" + "&fields=" + fields;
        
            //     HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            //     ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        
            //     // Return only the body of the response
            //     return response.getBody();
    }


    public List<BatchTicket> fetchTickets() throws Exception {
        SyncAuth syncAuth=syncAuthService.getSyncAuth();
        if (syncAuth == null) {
            throw new Exception("SyncAuth is null");
        }
        else {
            String url = syncAuth.getApi_url() + "/search?jql=";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Parse the response and map to Ticket objects
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            JsonNode issues = jsonResponse.get("issues");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

            List<BatchTicket> tickets = new ArrayList<>();
            for (JsonNode issue : issues) {

                // Check the issue type and skip bugs
                String issueType = issue.get("fields").get("issuetype").get("name").asText();
                if (issueType.equalsIgnoreCase("Bug")) {
                    continue; // Skip this iteration if the issue type is "Bug"
                }

                BatchTicket ticket = new BatchTicket();
//            Project project = projectRepository.findByJiraKey();
                ticket.setProjectKey(issue.get("fields").get("project").get("key").asText());
                // ticket.setProjectKey(issue.get("fields").get("project").get("key").asText());
                ticket.setJiraId(issue.get("id").asText());
                ticket.setSummary(issue.get("fields").get("summary").asText());
                ticket.setDescription(issue.get("fields").has("description") ? issue.get("fields").get("description").asText() : null);
                ticket.setStatus(issue.get("fields").get("status").get("name").asText());
                if (issue.get("fields").has("priority") && !issue.get("fields").get("priority").isNull()) {
                    ticket.setPriority(issue.get("fields").get("priority").get("name").asText());
                } else {
                    ticket.setPriority(null);
                }
                // Récupération des dates de création et de mise à jour
                String createdStr = issue.get("fields").get("created").asText();
                String updatedStr = issue.get("fields").get("updated").asText();
                LocalDateTime created = LocalDateTime.parse(createdStr, formatter);
                LocalDateTime updated = LocalDateTime.parse(updatedStr, formatter);

                ticket.setCreated(created);
                ticket.setUpdated(updated);

                // Récupération de le nom et prénom de l'assignee
                if (issue.get("fields").has("assignee") && !issue.get("fields").get("assignee").isNull()) {
                    JsonNode assigneeNode = issue.get("fields").get("assignee");
                    if (assigneeNode.has("displayName") && !assigneeNode.get("displayName").isNull()) {
                        ticket.setAssigneeName(assigneeNode.get("displayName").asText());
                    } else {
                        ticket.setAssigneeName(null);
                    }
                } else {
                    ticket.setAssigneeName(null);
                }

                // Extract story points
                if (issue.get("fields").has("customfield_10016") && !issue.get("fields").get("customfield_10016").isNull()) {
                    ticket.setCharge(issue.get("fields").get("customfield_10016").asDouble());
                    ticket.setReevaluatedCharge(issue.get("fields").get("customfield_10016").asDouble());
                    ticket.setAssignedCharge(issue.get("fields").get("customfield_10016").asDouble());
                } else if (issue.get("fields").has("customfield_10032") && !issue.get("fields").get("customfield_10032").isNull()) {
                    ticket.setCharge(issue.get("fields").get("customfield_10032").asDouble());
                    ticket.setReevaluatedCharge(issue.get("fields").get("customfield_10032").asDouble());
                    ticket.setAssignedCharge(issue.get("fields").get("customfield_10032").asDouble());
                } else {
                    ticket.setCharge(null);
                    ticket.setAssignedCharge(null);
                    ticket.setReevaluatedCharge(null);
                }

//            if (issue.get("fields").has("comment") && !issue.get("fields").get("comment").isNull()) {
//                        JsonNode commentsNode = issue.get("fields").get("comment").get("comments");
//                        if (commentsNode.size() > 0) {
//                            ticket.setComment(commentsNode.get(0).get("body").asText());
//                        } else {
//                            ticket.setComment(null);
//                        }
//                    } else {
//                        ticket.setComment(null);
//            }


                tickets.add(ticket);
            }
            return tickets;
        }

    }
    

    public List<BatchTicket> fetchTicketsAssignedToMyEmail() throws Exception {
        // Récupération de l'email de l'utilisateur actuel (supposant que l'authentification est configurée)
        String currentUserEmail = "Chaimae Rachdi";

        // Récupération des tickets assignés à l'email de l'utilisateur actuel
        List<BatchTicket> allTickets = fetchTickets();
        return allTickets.stream()
                         .filter(ticket -> currentUserEmail.equals(ticket.getAssigneeName()))
                         .collect(Collectors.toList());
    }



    public List<BatchTicket> fetchTicketsByProject(String projectKey) throws Exception {
        SyncAuth syncAuth=syncAuthService.getSyncAuth();

        String url = syncAuth.getApi_url() + "/search?jql=project=";
        String urlProject = url+projectKey;
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(urlProject, HttpMethod.GET, entity, String.class);
    
        // Parse the response and map to Ticket objects
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        JsonNode issues = jsonResponse.get("issues");
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
        List<BatchTicket> tickets = new ArrayList<>();
        for (JsonNode issue : issues) {
            BatchTicket ticket = new BatchTicket();
            ticket.setProjectKey(issue.get("fields").get("project").get("key").asText());
            ticket.setJiraId(issue.get("id").asText());
            ticket.setSummary(issue.get("fields").get("summary").asText());
            ticket.setDescription(issue.get("fields").has("description") ? issue.get("fields").get("description").asText() : null);
            ticket.setStatus(issue.get("fields").get("status").get("name").asText());
    
            
                // Récupération des dates de création et de mise à jour
        String createdStr = issue.get("fields").get("created").asText();
        String updatedStr = issue.get("fields").get("updated").asText();
        LocalDateTime created = LocalDateTime.parse(createdStr, formatter);
        LocalDateTime updated = LocalDateTime.parse(updatedStr, formatter);

        ticket.setCreated(created);
        ticket.setUpdated(updated);

        // Récupération de l'email de l'assignee
        // Vérification et récupération de l'email de l'assignee
        if (issue.get("fields").has("assignee") && !issue.get("fields").get("assignee").isNull()) {
            JsonNode assigneeNode = issue.get("fields").get("assignee");
            if (assigneeNode.has("emailAddress") && !assigneeNode.get("emailAddress").isNull()) {
                ticket.setAssigneeName(assigneeNode.get("emailAddress").asText());
            } else {
                ticket.setAssigneeName(null);
            }
        } else {
            ticket.setAssigneeName(null);
        }    
            tickets.add(ticket);
        }
    
        return tickets;
    }
    
}

