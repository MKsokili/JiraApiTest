package com.example.jiratestapi.SyncAuth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/connection")
public class SyncAuthController {

    @PostMapping("/")
    public ResponseEntity<String> Connect(){
        return new ResponseEntity<>("SReing", HttpStatus.OK)
    }

}
