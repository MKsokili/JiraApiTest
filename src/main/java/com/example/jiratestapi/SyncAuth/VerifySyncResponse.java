package com.example.jiratestapi.SyncAuth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@AllArgsConstructor
@Data
public class VerifySyncResponse {
    @JsonProperty("isConnected")
Boolean isConnected;
    @JsonProperty("data")
    SyncAuth data;


    public Optional<SyncAuth> getSyncAuth() {
        return Optional.ofNullable(data);
    }
}
