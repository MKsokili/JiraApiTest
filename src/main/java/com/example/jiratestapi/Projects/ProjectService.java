package com.example.jiratestapi.Projects;

import com.example.jiratestapi.SyncAuth.SyncAuth;
import com.example.jiratestapi.SyncAuth.SyncAuthService;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProjectService {
    ProjectRepository projectRepository;
    SyncAuthService syncAuthService;


    private org.springframework.http.HttpHeaders createHeaders() {
        SyncAuth syncAuth=syncAuthService.getSyncAuthInstant();
        String auth = syncAuth.getEmail() + ":" + syncAuth.getToken();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    public ResponseWithMsg addProjectKey(Long projectId,String prjctKey) throws Exception {
        Optional<Project> project=projectRepository.findById(projectId);
        boolean isConneted= doesProjectKeyExist(prjctKey);
        Optional<Project> projectWithSameJiraKey= (projectRepository.findByJiraKey(prjctKey));
        if(isConneted && projectWithSameJiraKey.isPresent())return new ResponseWithMsg(false,"This  Key already linked with Another project");
        if(!project.isPresent()) return new ResponseWithMsg(false,"Project not Found");
        if(!isConneted) return new ResponseWithMsg(false,"Project Key is not valid");

            project.get().setJiraKey(prjctKey);
            project.get().setIsValid(true);
            projectRepository.save(project.get());
            return  new ResponseWithMsg(true,"Projet Key is saved Successfully");



    }



    // Method to check if a project key exists
    public boolean doesProjectKeyExist(String projectKey) throws Exception {
        SyncAuth syncAuth=syncAuthService.getSyncAuthInstant();
        String urlString = syncAuth.getApiUrl()+"/rest/api/2/project";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(urlString, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();
//        System.out.println(responseBody);
        JSONArray projects = new JSONArray(responseBody);

        for (int i = 0; i < projects.length(); i++) {
            JSONObject project = projects.getJSONObject(i);
            if (project.getString("key").equals(projectKey)) {
                return true; // Project key exists
            }
        }

        return false; // Project key does not exist
    }
    public Response getJiraKey(Long projectId) throws Exception {
        Optional<Project> project=projectRepository.findById(projectId);
        String jiraKey=project.get().getJiraKey();
        boolean existsInJira = doesProjectKeyExist(jiraKey);// to delete later because we can get the validity from the database while it is stored => it is valid 4
        //the case where it will fail is where you change the jira key directly from the database  which is not logic


        return new Response(project.get().getIsValid()&&existsInJira,jiraKey);
    }
    public List<String> getProjectNamesWithIncompleteBatches() {
        // Retrieve all projects from the repository
        List<Project> projects = projectRepository.findAll();

        // Stream through the list of projects
        return projects.stream()

                .filter(Project::hasIncompleteBatch)

                .map(Project::getName)

                .collect(Collectors.toList());
    }
}
