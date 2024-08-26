package com.example.jiratestapi.Projects;

import com.example.jiratestapi.SyncAuth.SyncAuth;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class Response {
    @JsonProperty("isConnected")
    Boolean isConnected;
    @JsonProperty("jiraKey")
    String  jiraKey;

}
