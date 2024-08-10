package com.example.jiratestapi.Projects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseWithMsg {
    @JsonProperty("success")
    Boolean success ;
    @JsonProperty("msg")
    String msg;
}
