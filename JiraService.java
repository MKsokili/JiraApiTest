package com.example.jiratestapi.Jira;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class JiraService {

    @Autowired
     RestTemplate restTemplate;

    @Autowired
    private TicketRepository ticketRepository;


    private HttpHeaders createHeaders() {
        String auth = jiraUsername + ":" + jiraApiToken;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    public String getIssuesCreatedInLastWeek() {
        String url = jiraBaseUrl + "/search?jql=created >= -7d";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    public String createIssue(String projectKey, String summary, String description, String issueType) {
        String url = jiraBaseUrl + "/issue";
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
        String url = jiraBaseUrl + "/search?jql=";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }


    // public List<Ticket> fetchTickets(String apiToken) throws Exception {
    //     String url = jiraBaseUrl + "/search?jql=project=SCRUM";

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((jiraUsername + ":" + apiToken).getBytes()));
    //     headers.set("Content-Type", "application/json");

    //     HttpEntity<String> entity = new HttpEntity<>(headers);

    //     ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    //     // Parse the response and map to Ticket objects
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     JsonNode jsonResponse = objectMapper.readTree(response.getBody());
    //     JsonNode issues = jsonResponse.get("issues");

    //     List<Ticket> tickets = new ArrayList<>();
    //     for (JsonNode issue : issues) {
    //         Ticket ticket = new Ticket();
    //         ticket.setJiraId(issue.get("id").asText());
    //         ticket.setTitle(issue.get("fields").get("summary").asText());
    //         ticket.setDescription(issue.get("fields").get("description").asText());
    //         ticket.setStatus(issue.get("fields").get("status").get("name").asText());
    //         ticket.setCreatedDate(new Date(issue.get("fields").get("created").asText()));
    //         ticket.setUpdatedDate(new Date(issue.get("fields").get("updated").asText()));
    //         tickets.add(ticket);
    //     }

    //     return tickets;
    // }


    // public List<Ticket> fetchTickets() throws Exception {

    //     String url = jiraBaseUrl + "/search?jql=";
    //     HttpEntity<String> entity = new HttpEntity<>(createHeaders());
    //     ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    //     // Parse the response and map to Ticket objects
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     JsonNode jsonResponse = objectMapper.readTree(response.getBody());
    //     JsonNode issues = jsonResponse.get("issues");

    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    //     List<Ticket> tickets = new ArrayList<>();
    //     for (JsonNode issue : issues) {
    //         Ticket ticket = new Ticket();
    //         ticket.setJiraId(issue.get("id").asText());
    //         ticket.setTitle(issue.get("fields").get("summary").asText());
    //         ticket.setDescription(issue.get("fields").get("description").asText());
    //         ticket.setSummary(issue.get("fields").get("summary").asText());
    //         ticket.setStatus(issue.get("fields").get("status").get("name").asText());
    //         ZonedDateTime createdZonedDateTime = ZonedDateTime.parse(issue.get("fields").get("created").asText(), formatter);
    //         Date createdDate = Date.from(createdZonedDateTime.toInstant());
    //         ticket.setCreatedDate(createdDate);
    //         ZonedDateTime updatedZonedDateTime = ZonedDateTime.parse(issue.get("fields").get("updated").asText(), formatter);
    //         Date updatedDate = Date.from(updatedZonedDateTime.toInstant());
    //         ticket.setUpdatedDate(updatedDate);
    //         tickets.add(ticket);
    //     }

    //     return tickets;
    // }


    public List<Ticket> fetchTickets() throws Exception {
        String url = jiraBaseUrl + "/search?jql=";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    
        // Parse the response and map to Ticket objects
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        JsonNode issues = jsonResponse.get("issues");
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
        List<Ticket> tickets = new ArrayList<>();
        for (JsonNode issue : issues) {
            Ticket ticket = new Ticket();
            ticket.setProjectKey(issue.get("fields").get("project").get("key").asText());
            ticket.setJiraId(issue.get("id").asText());
            ticket.setTitle(issue.get("fields").get("summary").asText());
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
                ticket.setStoryPoints(issue.get("fields").get("customfield_10016").asInt());
            } else {
                ticket.setStoryPoints(null);
            }
        


            tickets.add(ticket);
        }
        return tickets;

    }
    

    public List<Ticket> fetchTicketsAssignedToMyEmail() throws Exception {
        // Récupération de l'email de l'utilisateur actuel (supposant que l'authentification est configurée)
        String currentUserEmail = "Chaimae Rachdi";

        // Récupération des tickets assignés à l'email de l'utilisateur actuel
        List<Ticket> allTickets = fetchTickets();
        return allTickets.stream()
                         .filter(ticket -> currentUserEmail.equals(ticket.getAssigneeName()))
                         .collect(Collectors.toList());
    }



    public List<Ticket> fetchTicketsByProject(String projectKey) throws Exception {
        String url = jiraBaseUrl + "/search?jql=project=";
        String urlProject = url+projectKey;
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(urlProject, HttpMethod.GET, entity, String.class);
    
        // Parse the response and map to Ticket objects
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        JsonNode issues = jsonResponse.get("issues");
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
        List<Ticket> tickets = new ArrayList<>();
        for (JsonNode issue : issues) {
            Ticket ticket = new Ticket();
            ticket.setProjectKey(projectKey);
            ticket.setJiraId(issue.get("id").asText());
            ticket.setTitle(issue.get("fields").get("summary").asText());
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

