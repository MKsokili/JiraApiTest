package com.example.jiratestapi.SyncAuth;

import com.example.jiratestapi.Jira.JiraService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SyncAuthService {
    @Autowired
    private SyncAuthRepository syncAuthRepository;
    @Autowired
    private RestTemplate restTemplate;


    public void create(String url,String token,String email) {
        SyncAuth auth = getSyncAuthInstant();
        auth.setEmail(email);
        auth.setIsConnected(true);
        auth.setIsStopped(false);
        auth.setToken(token);
        auth.setApiUrl(url);
        syncAuthRepository.save(auth);
    }

    public SyncAuth getSyncAuthInstant() {
        List<SyncAuth> syncAuthList = syncAuthRepository.findAll();
        if(syncAuthList.isEmpty()){
            return new SyncAuth();
        }
        return syncAuthList.get(0);
    }
    public Boolean checkIfConnected(SyncAuth syn) {
        String auth = syn.getEmail() + ":" + syn.getToken();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.set("Content-Type", "application/json");

        String url = syn.getApiUrl() + "/rest/api/2/myself";
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response != null && response.getStatusCode() == HttpStatus.OK) {
                return true; // Credentials are valid
            } else {
                // Handle invalid credentials or other issues
                System.err.println("Failed to connect: " + (response != null ? response.getStatusCode() : "No response") + " " + (response != null ? response.getBody() : ""));
                return false;
            }
        } catch (RestClientException e) {
            // Log the exception for debuggingSystem.err.println("Exception occurred while connecting: " + e.getMessage());
            return false;
        }
    }


    public VerifySyncResponse verifyIfConnected() {
        SyncAuth syncAuth = getSyncAuthInstant();

        if(syncAuth==null||syncAuth.getToken()==null||syncAuth.getApiUrl()==null){
            return new VerifySyncResponse(syncAuth.getIsStopped(),false,syncAuth);

        }
        Boolean res = checkIfConnected(syncAuth); // Handle Optional properly
        return new VerifySyncResponse(syncAuth.getIsStopped(),!syncAuth.getIsStopped()&&res, syncAuth);
    }
}
