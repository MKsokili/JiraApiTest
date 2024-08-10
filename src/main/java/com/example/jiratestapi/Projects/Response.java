package com.example.jiratestapi.SyncAuth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class VerifySyncResponse {
    @JsonProperty("isConnected")
    Boolean isConnected;
    @JsonProperty("data")
//
    Optional<SyncAuth> data;
}
